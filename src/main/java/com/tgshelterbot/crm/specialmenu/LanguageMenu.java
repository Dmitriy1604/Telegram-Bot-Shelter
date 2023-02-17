package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.Language;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;

@Component
public class LanguageMenu {
    private final Logger log = LoggerFactory.getLogger(LanguageMenu.class);

    private final LanguageRepository languageRepository;

    public LanguageMenu(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public SendMessage getLanguageMenu(@NotNull User user) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<Language> languageList = languageRepository.findAll();
        languageList.forEach(language -> {
                    keyboardMarkup.addRow(new InlineKeyboardButton(language.getName())
                            .callbackData(language.getId().toString()));
                }
        );

        log.debug("LanguageMenu: {}", keyboardMarkup);
        return new SendMessage(user.getTelegramId(), "Please select you language").replyMarkup(keyboardMarkup);
    }

}
