package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.specialmenu.ShelterMenu;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.repository.UserStateRepository;
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
    private final MessageSender messageSender;
    private final StartMenu startMenu;
    private final InlineBuilder inlineBuilder;
    private final UserService userService;
    private final SpecialService specialService;
    private final SupportService supportService;
    private final ShelterMenu shelterMenu;
    private final InlineMenuRepository inlineMenuRepository;
    private final UserStateRepository userStateRepository;
    private final ReportService reportService;
    private final LocalizedMessages lang;


    /**
     * Фасад, основная логика построения меню бота и обработки статусов
     * По окончанию метода, отправляем сообщение или редактируем, сохраняем ответ полученный от телеги
     *
     * @param update Update
     */
    public void processUpdate(Update update) {
        String message = getMessage(update);
        String tag = null;
        Long idUser = userService.getIdUser(update);
        User user = userService.findUserOrCreate(idUser);
        EditMessageText editInlineMessageText = null;
        logger.trace("idUser {}", idUser);

        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
            logger.debug("CallbackQuery: {}", tag);
        }

        // Дефотное сообщение, если мы не распознали команду пользователя
        SendMessage sendMessage = new SendMessage(idUser, lang.get("start", user));

        // Обработка команды /start, начальная точка работы бота
        if (message.startsWith("/start")) {
            user.setShelter(null);
            user.setLanguage(1L);
            user.setStateId(userStateRepository.findFirstByTagSpecial(UserStateSpecial.SELECT_SHELTER).orElse(null));
            user.setLastResponseStatemenuId(null);
            messageSender.sendMessage(shelterMenu.getShelterMenu(user), user);
            return;
        }
        if (message.startsWith("/test")) {
            logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            // метод для тестирования
            messageSender.sendMessage(sendMessage, user);
            return;
        }

        // Выйти в главное меню, с удалением ReplyKeyboard???
        if (message.startsWith("\uD83D\uDD1A")) {
            messageSender.sendMessage(new SendMessage(idUser, lang.get("chat_end", user)).replyMarkup(new ReplyKeyboardRemove())
                    , user);
            messageSender.sendMessage(startMenu.getSendMessageStartMenu(user), user);
            return;
        }

        // Выход в главное меню по кнопке инлайн
        if (tag != null && tag.equals("EXIT")) {
            DeleteMessage deleteMessage = new DeleteMessage(user.getTelegramId(), user.getLastResponseStatemenuId().intValue());
            bot.execute(deleteMessage);

            SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
            messageSender.sendMessage(sendMessageStartMenu, user);
            return;
        }

        // Отвечает сотрудник чата поддержки. Не логируем, ничего не сохраняем
        if (supportService.isSupportChat(update)) {
            bot.execute(supportService.sendToUser(update));
            return;
        }

        // Обработка специальных статусов
        if (user.getStateId() != null && user.getStateId().getTagSpecial() != null) {
            specialService.checkSpecialStatus(user, update);
            return;
        }

        // есть callback_data
        if (tag != null) {
            Optional<InlineMenu> menuOptional = inlineMenuRepository
                    .findFirstByLanguageIdAndShelterIdAndTagCallback(
                            user.getLanguage(),
                            user.getShelter(),
                            tag
                    );
            // Получаем меню из базы по TagCallback
            if (menuOptional.isEmpty() && user.getStateId().getTagSpecial() != null) {
                specialService.checkSpecialStatus(user, update);
                return;
            }
            if (menuOptional.isPresent()) {
                InlineMenu menu = menuOptional.get();
                message = menu.getAnswer();
                // Сетим новый статус
                if (menu.getStateIdNext() != null) {
                    user.setStateId(menu.getStateIdNext());
                }
                // Обработка кнопки меню, когда есть специальный статус(действие/меню) по нажатию на кнопку
                if (user.getStateId().getTagSpecial() != null) {
                    specialService.checkSpecialStatusInMenu(user, update, menu);
                    return;
                }

                // Формируем динамическое меню
                InlineKeyboardMarkup inlineMenu = inlineBuilder.getInlineMenu(menu);
                editInlineMessageText = new EditMessageText(idUser,
                        user.getLastResponseStatemenuId().intValue(),
                        message).replyMarkup(inlineMenu);
                messageSender.sendMessage(editInlineMessageText, user);
                return;
            }
        }

        //Отправим сообщение на случай если сбой обработки
        try {
            messageSender.sendMessage(sendMessage, user);
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
        }

    }

    /**
     * Инициализация сообщения
     * @param update событие на сервере
     * @return message текст написанный в чат
     */
    private String getMessage(Update update) {
        String message = "^-^";
        if (update.message() != null && update.message().text() != null) {
            message = update.message().text();
        }
        return message;
    }

}