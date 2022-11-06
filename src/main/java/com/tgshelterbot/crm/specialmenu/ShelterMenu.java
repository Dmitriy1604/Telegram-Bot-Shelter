package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class ShelterMenu {
    private final Logger log = LoggerFactory.getLogger(ShelterMenu.class);
    private final ShelterRepository shelterRepository;
    private final TelegramBot bot;
    private final UserService userService;

    public ShelterMenu(ShelterRepository shelterRepository, TelegramBot bot, UserService userService) {
        this.shelterRepository = shelterRepository;
        this.bot = bot;
        this.userService = userService;
    }

    public SendMessage getShelterMenu(@NotNull User user) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<Shelter> shelterList = shelterRepository.findAll();
        shelterList.forEach(language -> {
                    keyboardMarkup.addRow(new InlineKeyboardButton(language.getName())
                            .callbackData(language.getId().toString()));
                }
        );

        log.debug("ShelterMenu: {}", keyboardMarkup);
        return new SendMessage(user.getTelegramId(),"Выберите приют").replyMarkup(keyboardMarkup);
    }

    public void updateShelter(User user, String tag) {
        user.setShelter(Long.parseLong(tag));
        userService.update(user);
        DeleteMessage deleteMessage = new DeleteMessage(user.getTelegramId(), user.getLastResponseStatemenuId().intValue());
        bot.execute(deleteMessage);
    }
}
