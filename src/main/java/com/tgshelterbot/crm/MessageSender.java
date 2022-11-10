package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSender {
    private final TelegramBot bot;
    private final UserService userService;

    public User sendMessage(SendMessage message, User user) {
        SendResponse execute = bot.execute(message);
        if (execute != null) {
            user.setLastResponseStatemenuId(execute.message().messageId().longValue());
        }
        User update = userService.update(user);
        log.debug("User after send and save: {}", user);
        return update;
    }

    public User sendMessage(EditMessageText message, User user) {
        SendResponse execute = (SendResponse) bot.execute(message);
        if (execute != null) {
            user.setLastResponseStatemenuId(execute.message().messageId().longValue());
        }
        User update = userService.update(user);
        log.debug("User after send and save: {}", user);
        return update;
    }

    public User sendMessage(SendDocument message, User user) {
        SendResponse execute = bot.execute(message);
        if (execute != null) {
            user.setLastResponseStatemenuId(execute.message().messageId().longValue());
        }
        User update = userService.update(user);
        log.debug("User after send and save: {}", user);
        return update;
    }

}
