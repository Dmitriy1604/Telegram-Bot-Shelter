package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.crm.specialmenu.SpecialService;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class TelegramFacade {

    private final Logger logger = LoggerFactory.getLogger(TelegramFacade.class);

    private final TelegramBot bot;
    private final StartMenu startMenu;
    private final InlineBuilder inlineBuilder;
    private final UserService userService;
    private final SpecialService specialService;
    private final SupportService supportService;
    private final InlineMenuRepository inlineMenuRepository;
    private final UserStateRepository userStateRepository;



    /**
     * Фасад, основная логика построения меню бота и обработки статусов
     * По окончанию метода, отправляем сообщение или редактируем, сохраняем ответ полученный от телеги
     *
     * @param update Update
     */
    public void processUpdate(Update update) {
        // Готовы отправить сообщение. Отправка и обработка ответа, в самом конце метода и обновление юзера
        boolean isReadyToSend = false;
        String message = getMessage(update);
        Long idUser = userService.getIdUser(update);
        User user = userService.findUserOrCreate(idUser);
        SendResponse execute = null;
        EditMessageText editInlineMessageText = null;
        logger.trace("idUser {}", idUser);

        // Дефотное сообщение, если мы не распознали команду пользователя
        SendMessage sendMessage = new SendMessage(idUser, message);

        // Обработка команды /start, начальная точка работы бота
        if (message.startsWith("/start")) {
            user.setShelter(null);
            user.setStateId(userStateRepository.findFirstByTagSpecial(UserStateSpecial.SELECT_SHELTER).orElse(null));
            user.setLastResponseStatemenuId(null);
            bot.execute(new SendMessage(idUser, "Стартовое хаюшки тебе").replyMarkup(new ReplyKeyboardRemove()));
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

            Optional<InlineMenu> menuOptional = inlineMenuRepository
                    .findFirstByLanguageIdAndShelterIdAndTagCallback(
                            user.getLanguage(),
                            user.getShelter(),
                            tag
                    );
            // Получаем меню из базы по TagCallback
            if (menuOptional.isEmpty() && user.getStateId().getTagSpecial() != null) {
                sendMessage = specialService.checkSpecialStatus(user, update);
                isReadyToSend = true;
            }
            if (!isReadyToSend && menuOptional.isPresent()) {
                InlineMenu menu = menuOptional.get();
                message = menu.getAnswer();
                // Сетим новый статус
                if (menu.getStateIdNext() != null) {
                    user.setStateId(menu.getStateIdNext());
                }
                // Обработка кнопки меню, когда есть специальный статус(действие/меню) по нажатию на кнопку
                if (user.getStateId().getTagSpecial() != null) {
                    sendMessage = specialService.checkSpecialStatusInMenu(user, update, menu);
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

    private String getMessage(Update update) {
        String message = "Я Вас не понял, нажмите /start для возврата в главное меню";
        if (update.message() != null && update.message().text() != null) {
            message = update.message().text();
        }
        return message;
    }

}