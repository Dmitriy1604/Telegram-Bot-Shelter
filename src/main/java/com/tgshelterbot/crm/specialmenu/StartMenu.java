package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.InlineBuilder;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Component
public class StartMenu {
    private final Logger log = LoggerFactory.getLogger(StartMenu.class);

    private final LanguageMenu languageMenu;
    private final ShelterMenu shelterMenu;
    private final InlineMenuRepository inlineMenuRepository;
    private final InlineBuilder inlineBuilder;
    private final UserService userService;

    public StartMenu(LanguageMenu languageMenu, ShelterMenu shelterMenu, InlineMenuRepository inlineMenuRepository, InlineBuilder inlineBuilder, UserService userService) {
        this.languageMenu = languageMenu;
        this.shelterMenu = shelterMenu;
        this.inlineMenuRepository = inlineMenuRepository;
        this.inlineBuilder = inlineBuilder;
        this.userService = userService;
    }

    public SendMessage getStartMenu(@NotNull User user) {
        // Проверим на выбор языка, выдадим список языков
        if (user.getLanguage() == null) {
            return languageMenu.getLanguageMenu(user);
        }

        // Проверим на выбор приюта, выдадим список приютов
        if (user.getShelter() == null) {
            return shelterMenu.getShelterMenu(user);
        }

        String defaultMsg = "";
        InlineMenu inlineMenu = new InlineMenu();
        Optional<InlineMenu> menuOptional = inlineMenuRepository.findFirstByLanguageIdAndAndShelterIdAndQuestion(user.getLanguage(), user.getShelter(),
                "/start");
        if (menuOptional.isPresent()) {
            inlineMenu = menuOptional.get();
            defaultMsg = menuOptional.get().getAnswer();
            user.setStateId(inlineMenu.getStateIdNext());
            userService.update(user);
        }

        InlineKeyboardMarkup keyboardMarkup = inlineBuilder.getInlineMenu(inlineMenu);
        System.out.println("inlineMenu = " + keyboardMarkup);
        return new SendMessage(user.getTelegramId(), defaultMsg).replyMarkup(keyboardMarkup);
    }
}
