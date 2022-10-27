package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.Language;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.ShelterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class ShelterMenu {
    private final Logger log = LoggerFactory.getLogger(ShelterMenu.class);
    private final ShelterRepository shelterRepository;

    public ShelterMenu(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public SendMessage getShelterMenu(@NotNull User user) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<Shelter> languageList = shelterRepository.findAll();
        languageList.forEach(language -> {
                    keyboardMarkup.addRow(new InlineKeyboardButton(language.getName())
                            .callbackData(language.getId().toString()));
                }
        );

        log.debug("ShelterMenu: {}", keyboardMarkup);
        return new SendMessage(user.getTelegramId(), "Выберите приют").replyMarkup(keyboardMarkup);
    }
}
