package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.InlineBuilder;
import com.tgshelterbot.crm.UserService;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.InlineMenuRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StartMenu {
    private final Logger log = LoggerFactory.getLogger(StartMenu.class);
    private final InlineMenuRepository inlineMenuRepository;
    private final InlineBuilder inlineBuilder;
    private final UserService userService;

    /*TODO вынести в пропертя*/
    public EditMessageText getEditMessageStartMenu(@NotNull User user) {
        String defaultMsg = "Я Вас не понял, нажмите /start для возврата в главное меню";
        InlineMenu inlineMenu = new InlineMenu();
        Optional<InlineMenu> menuOptional = inlineMenuRepository.findFirstByLanguageIdAndShelterIdAndQuestion(user.getLanguage(), user.getShelter(),
                "/start");
        if (menuOptional.isPresent()) {
            inlineMenu = menuOptional.get();
            defaultMsg = menuOptional.get().getAnswer();
            user.setStateId(inlineMenu.getStateIdNext());
            userService.update(user);
        }

        InlineKeyboardMarkup keyboardMarkup = inlineBuilder.getInlineMenu(inlineMenu);
        return new EditMessageText(user.getTelegramId(), user.getLastResponseStatemenuId().intValue(), defaultMsg)
                .replyMarkup(keyboardMarkup);
    }

    public SendMessage getSendMessageStartMenu(@NotNull User user) {
        String defaultMsg = "Я Вас не понял, нажмите /start для возврата в главное меню";
        InlineMenu inlineMenu = new InlineMenu();
        Optional<InlineMenu> menuOptional = inlineMenuRepository.findFirstByLanguageIdAndShelterIdAndQuestion(user.getLanguage(), user.getShelter(),
                "/start");
        if (menuOptional.isPresent()) {
            inlineMenu = menuOptional.get();
            defaultMsg = menuOptional.get().getAnswer();
            user.setStateId(inlineMenu.getStateIdNext());
            userService.update(user);
        }

        InlineKeyboardMarkup keyboardMarkup = inlineBuilder.getInlineMenu(inlineMenu);
        return new SendMessage(user.getTelegramId(), defaultMsg).replyMarkup(keyboardMarkup);
    }

}
