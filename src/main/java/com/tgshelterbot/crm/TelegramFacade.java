package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.crm.specialmenu.ShelterMenu;
import com.tgshelterbot.crm.specialmenu.SpecialService;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TelegramFacade {

    private final Logger logger = LoggerFactory.getLogger(TelegramFacade.class);

    private final TelegramBot bot;
    private final StartMenu startMenu;
    private final ShelterMenu shelterMenu;
    private final UserService userService;
    private final InlineBuilder inlineBuilder;
    private final InlineMenuRepository inlineMenuRepository;
    private final SpecialService specialService;
    private final SupportService supportService;

    public TelegramFacade(TelegramBot bot, StartMenu startMenu, ShelterMenu shelterMenu, UserService userService, InlineBuilder inlineBuilder, InlineMenuRepository inlineMenuRepository, SpecialService specialService, SupportService supportService) {
        this.bot = bot;
        this.startMenu = startMenu;
        this.shelterMenu = shelterMenu;
        this.userService = userService;
        this.inlineBuilder = inlineBuilder;
        this.inlineMenuRepository = inlineMenuRepository;
        this.specialService = specialService;
        this.supportService = supportService;
    }

    /**
     * Фасад, основная логика построения меню бота и обработки статусов
     * По окончанию метода, отправляем сообщение или редактируем, сохраняем ответ полученный от телеги
     *
     * @param update Update
     */
    public void processUpdate(Update update) {
        // Готовы отправить сообщение. Отправка и обработка ответа, в самом конце метода и обновление юзера
        boolean isReadyToSend = false;
        Long idUser = 0L;
        String message = "Я Вас не понял, нажмите /start для возврата в главное меню";
        SendResponse execute = null;
        EditMessageText editInlineMessageText = null;


        if (update.message() != null && update.message().chat() != null && update.message().chat().id() != null) {
            idUser = update.message().chat().id();
        } else if (update.callbackQuery() != null) {
            idUser = update.callbackQuery().from().id();
        }
        User user = userService.findUserOrCreate(idUser);
        logger.trace("idUser {}", idUser);

        // Дефотное сообщение, если мы не распознали команду пользователя
        SendMessage sendMessage = new SendMessage(idUser, message);


        if (update.message() != null && update.message().text() != null) {
            message = update.message().text();
            logger.debug("MESSAGE: {} , idUser: {}", message, user);
        }

        // Обработка команды /start, начальная точка работы бота
        if (message.startsWith("/start")) {
            user.setShelter(null);
            user.setStateId(null);
            user.setLastResponseStatemenuId(null);
            bot.execute(new SendMessage(idUser, "Стартовое хаюшки тебе").replyMarkup(new ReplyKeyboardRemove()));
            sendMessage = startMenu.getStartMenu(user);
            isReadyToSend = true;
        }

        // Выйти в главное меню, с удалением ReplyKeyboard???
        if (message.startsWith("\uD83D\uDD1A EXIT")) {
            bot.execute(new SendMessage(idUser, "Чат закончен, спасибки").replyMarkup(new ReplyKeyboardRemove()));
            sendMessage = startMenu.getStartMenu(user);
            isReadyToSend = true;
        }

        // Отвечает сотрудник чата поддержки. Не логируем, ничего не сохраняем
        if (!isReadyToSend && supportService.isSupportChat(update)) {
            bot.execute(supportService.sendToUser(update));
            return;
        }

        // Обработка специальных статусов
        SendMessage specialStatus = null;
        if (!isReadyToSend && user.getStateId() != null && user.getStateId().getTagSpecial() != null) {
            specialStatus = specialService.checkSpecialStatus(user, update);
            if (specialStatus != null) {
                sendMessage = specialStatus;
                isReadyToSend = true;
            }
        }


        // есть callback_data
        if (!isReadyToSend && update.callbackQuery() != null) {
            String tag = update.callbackQuery().data();
            logger.debug("CallbackQuery: {}", tag);

            // Обработка выбора языка
            /*
            if (user.getLanguage() == null) {
                user.setLanguage(Long.parseLong(tag));
                SendResponse execute = bot.execute(startMenu.getStartMenu(user));
                user.setLastResponseStatemenuId(execute.message().messageId().longValue());
                userService.update(user);
                return;
            }
            */

            // Обработка выбора приюта
            if (user.getShelter() == null) {
                shelterMenu.updateShelter(user, tag);
                sendMessage = startMenu.getStartMenu(user);
                isReadyToSend = true;
            }

            Optional<InlineMenu> menuOptional = inlineMenuRepository
                    .findFirstByLanguageIdAndAndShelterIdAndTagCallback(
                            user.getLanguage(),
                            user.getLanguage(),
                            tag
                    );
            // Получаем меню из базы по TagCallback
            if (!isReadyToSend && menuOptional.isPresent()) {
                InlineMenu menu = menuOptional.get();
                message = menu.getAnswer();
                // Сетим новый статус
                if (menu.getStateIdNext() != null) {
                    user.setStateId(menu.getStateIdNext());
                }
                // Обработка кнопки меню, когда есть специальный статус(действие/меню) по нажатию на кнопку
                if (user.getStateId().getTagSpecial() != null) {
                    sendMessage = specialService.checkSpecialStatusInMenu(user, user.getStateId().getTagSpecial(), menu);
                    isReadyToSend = true;
                }
                // Формируем динамическое меню
                if (!isReadyToSend) {
                    InlineKeyboardMarkup inlineMenu = inlineBuilder.getInlineMenu(menu);
                    // Есть клавиатура, которую мы должны отредактировать
                    if (user.getLastResponseStatemenuId() != null) {
                        // собрали клавиатуру и она имеет кнопки
                        if (inlineMenu.inlineKeyboard().length > 0) {
                            editInlineMessageText = new EditMessageText(idUser,
                                    user.getLastResponseStatemenuId().intValue(),
                                    message).replyMarkup(inlineMenu);
                        } else {
                            // Клавиатура не имеет кнопок, отредактируем текст прошлого сообщения
                            editInlineMessageText = new EditMessageText(idUser,
                                    user.getLastResponseStatemenuId().intValue(),
                                    message);
                        }
                    } else {
                        // Или клавиатуры для редактирования, отправим новую
                        sendMessage = new SendMessage(idUser, message).replyMarkup(inlineMenu);
                    }
                }
            }
        }

        //Отправим сообщение или отредактируем
        try {
            if (editInlineMessageText != null) {
                execute = (SendResponse) bot.execute(editInlineMessageText);
            } else if (sendMessage != null) {
                execute = bot.execute(sendMessage);
            }
            logger.debug("execute, {}", execute);
            if (execute != null) {
                user.setLastResponseStatemenuId(execute.message().messageId().longValue());
            }
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
        }
        userService.update(user);
    }

}