package com.tgshelterbot.crm;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.repository.InlineMenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class InlineBuilder {
    private final Logger log = LoggerFactory.getLogger(InlineBuilder.class);
    private final InlineMenuRepository inlineMenuRepository;

    public InlineBuilder(InlineMenuRepository inlineMenuRepository) {
        this.inlineMenuRepository = inlineMenuRepository;
    }


    public InlineKeyboardMarkup getInlineMenu(InlineMenu menu) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        UserState state;

        if (menu.getStateIdNext() != null) {
            state = menu.getStateIdNext();
        } else {
            state = menu.getStateId();
        }

        List<InlineMenu> answerList = inlineMenuRepository.findAllByStateId(state);
        answerList.sort(Comparator.comparing(InlineMenu::getPriority));
        answerList.forEach(telegramAnswer -> {
            if (telegramAnswer.getButton() != null
                    && telegramAnswer.getTagCallback() != null
                    && telegramAnswer.getButton().length() > 0
                    && telegramAnswer.getTagCallback().length() > 0
            ) {
                keyboardMarkup.addRow(new InlineKeyboardButton(telegramAnswer.getButton())
                        .callbackData(telegramAnswer.getTagCallback()));
            }
        });

        log.debug("InlineKeyboardMarkup: {}", keyboardMarkup);
        return keyboardMarkup;
    }
}
