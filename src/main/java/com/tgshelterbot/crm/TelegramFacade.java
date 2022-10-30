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
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

    public TelegramFacade(TelegramBot bot, StartMenu startMenu, ShelterMenu shelterMenu, UserService userService, InlineBuilder inlineBuilder, InlineMenuRepository inlineMenuRepository, SpecialService specialService) {
        this.bot = bot;
        this.startMenu = startMenu;
        this.shelterMenu = shelterMenu;
        this.userService = userService;
        this.inlineBuilder = inlineBuilder;
        this.inlineMenuRepository = inlineMenuRepository;
        this.specialService = specialService;
    }

    /**
     * Для различных вариантов сообщений будем уходить в свои обработчики
     *
     * @param update
     */
    public void processUpdate(Update update) {
        boolean isReadyToSend = false;
        Long idUser = 0L;
        logger.warn("idUser {}", idUser);
        String message = "Я Вас не понял, нажмите /start для возврата в главное меню";
        SendResponse execute = null;
        EditMessageText editInlineMessageText = null;


        if (update.message() != null && update.message().chat() != null && update.message().chat().id() != null) {
            idUser = update.message().chat().id();
        } else if (update.callbackQuery() != null) {
            idUser = update.callbackQuery().from().id();
        }
        User user = userService.findUserOrCreate(idUser);
        //logger.debug("user: {}", user);

        SendMessage sendMessage = new SendMessage(idUser, message);


        if (update.message() != null && update.message().text() != null) {
            message = update.message().text();
            logger.debug("MESSAGE: {} , idUser: {}", message, user);
        }

        if (message.startsWith("/start")) {
            user.setShelter(null);
            user.setStateId(null);
            user.setLastResponseStatemenuId(null);
            bot.execute(new SendMessage(idUser, "Стартовое хаюшки тебе").replyMarkup(new ReplyKeyboardRemove()));
            sendMessage = startMenu.getStartMenu(user);
            isReadyToSend = true;
        }

        if (message.startsWith("\uD83D\uDD1A EXIT")) {
            bot.execute(new SendMessage(idUser, "Чат закончен, спасибки").replyMarkup(new ReplyKeyboardRemove()));
            sendMessage = startMenu.getStartMenu(user);
            isReadyToSend = true;
        }

        // Обработка специальных статусов
        SendMessage specialStatus = null;
        if (!isReadyToSend) {
            specialStatus = specialService.checkSpecialStatus(user, update, null);
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

            if (!isReadyToSend && menuOptional.isPresent()) {
                InlineMenu menu = menuOptional.get();
                message = menu.getAnswer();
                // Сетим статус
                if (menu.getStateId() != null) {
                    user.setStateId(menu.getStateId());
                }
                if (menu.getStateIdNext() != null) {
                    user.setStateId(menu.getStateIdNext());
                }
                /*TODO Сделать обработку для специальных статусов получения телефона, репортов и т.п.*/
                if (menu.getUserStateSpecial() != null && !menu.getUserStateSpecial().equals(UserStateSpecial.MAIN)) {
                    sendMessage = specialService.checkSpecialStatus(user, update, menu);
                    isReadyToSend = true;
                }
                if (!isReadyToSend) {
                    InlineKeyboardMarkup inlineMenu = inlineBuilder.getInlineMenu(menu);
                    if (user.getLastResponseStatemenuId() != null) {
                        // Если есть клавиатура
                        if (inlineMenu.inlineKeyboard().length > 0) {
                            editInlineMessageText = new EditMessageText(idUser,
                                    user.getLastResponseStatemenuId().intValue(),
                                    message).replyMarkup(inlineMenu);
                        } else {
                            editInlineMessageText = new EditMessageText(idUser,
                                    user.getLastResponseStatemenuId().intValue(),
                                    message);
                        }
                    } else {
                        sendMessage = new SendMessage(idUser, message).replyMarkup(inlineMenu);
                    }
                }
            }
        }

        //Отправим сообщение или отредактируем
        try {
            if (editInlineMessageText != null) {
                execute = (SendResponse) bot.execute(editInlineMessageText);
            } else {
                execute = bot.execute(sendMessage);
            }
            logger.debug("execute, {}", execute);
            user.setLastResponseStatemenuId(execute.message().messageId().longValue());
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
        }
        userService.update(user);
    }

}