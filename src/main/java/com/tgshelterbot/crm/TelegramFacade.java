package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
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
    private final UserService userService;
    private final InlineBuilder inlineBuilder;
    private final InlineMenuRepository inlineMenuRepository;

    public TelegramFacade(TelegramBot bot, StartMenu startMenu, UserService userService, InlineBuilder inlineBuilder, InlineMenuRepository inlineMenuRepository) {
        this.bot = bot;
        this.startMenu = startMenu;
        this.userService = userService;
        this.inlineBuilder = inlineBuilder;
        this.inlineMenuRepository = inlineMenuRepository;
    }

    /**
     * Для различных вариантов сообщений будем уходить в свои обработчики
     *
     * @param update
     */
    public void processUpdate(Update update) {
        Long idUser = 0L;
        String message = "Я Вас не понял, нажмите /start для возврата в главное меню";
        if (update.message() != null && update.message().chat() != null && update.message().chat().id() != null) {
            idUser = update.message().chat().id();
        } else if (update.callbackQuery() != null) {
            idUser = update.callbackQuery().from().id();
        }
        User user = userService.findUserOrCreate(idUser);
        logger.debug("user: {}", user);

        SendMessage sendMessage = new SendMessage(idUser, message);

        if (update.message() != null && update.message().text() != null) {
            message = update.message().text();
            logger.debug("MESSAGE: {} , idUser: {}", message, user);
        }

        if (message.startsWith("/start")) {
            user.setShelter(null);
            user.setStateId(null);
            user.setLastResponseStatemenuId(null);
            SendResponse execute = bot.execute(startMenu.getStartMenu(user));
            user.setLastResponseStatemenuId(execute.message().messageId().longValue());
            userService.update(user);
            return;
        }

        // есть callback_data
        if (update.callbackQuery() != null) {
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
                user.setShelter(Long.parseLong(tag));

                DeleteMessage deleteMessage = new DeleteMessage(idUser, user.getLastResponseStatemenuId().intValue());
                bot.execute(deleteMessage);

                SendResponse execute = bot.execute(startMenu.getStartMenu(user));
                user.setLastResponseStatemenuId(execute.message().messageId().longValue());
                userService.update(user);
                return;
            }
            /*TODO Сделать обработку для специальных статусов получения телефона, репортов и т.п.*/

            Optional<InlineMenu> menu = inlineMenuRepository.findFirstByTagCallback(tag); /*TODO переделать поиск с учетом приюта*/
            /*TODO зарефакторить говнокод*/
            if (menu.isPresent()) {
                SendResponse execute;
                message = menu.get().getAnswer();
                // Сетим статус
                if (menu.get().getStateId() != null) {
                    user.setStateId(menu.get().getStateId());
                }
                if (menu.get().getStateIdNext() != null) {
                    user.setStateId(menu.get().getStateIdNext());
                }

                InlineKeyboardMarkup inlineMenu = inlineBuilder.getInlineMenu(menu.get());
                logger.debug("!!!!inlineMenu::" + inlineMenu);
                if (user.getLastResponseStatemenuId() != null) {
                    // Если есть клавиатура
                    if (inlineMenu.inlineKeyboard().length > 0) {
                        EditMessageText editInlineMessageText = new EditMessageText(idUser,
                                user.getLastResponseStatemenuId().intValue(),
                                message).replyMarkup(inlineMenu);
                        BaseResponse execute1 = bot.execute(editInlineMessageText);
                        logger.debug("!!!!execute1::" + execute1);
                        userService.update(user);
                        return;
                    } else {
                        EditMessageText editInlineMessageText = new EditMessageText(idUser,
                                user.getLastResponseStatemenuId().intValue(),
                                message);
                        BaseResponse execute1 = bot.execute(editInlineMessageText);
                        logger.debug("!!!!execute1::" + execute1);
                        userService.update(user);
                        return;
                    }
                } else {
                    sendMessage = new SendMessage(idUser, message).replyMarkup(inlineMenu);
                    execute = bot.execute(sendMessage);
                    user.setLastResponseStatemenuId(execute.message().messageId().longValue());
                    logger.debug("EXECUTE::" + execute);
                }
                userService.update(user);
                return;
            }
        }
        bot.execute(sendMessage);
    }
}
