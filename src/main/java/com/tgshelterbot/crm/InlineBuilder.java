package com.tgshelterbot.crm;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.tgshelterbot.model.AnimalReportType;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.repository.InlineMenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Построение меню бота
 */
@Service
public class InlineBuilder {
    private final Logger log = LoggerFactory.getLogger(InlineBuilder.class);
    private final InlineMenuRepository inlineMenuRepository;

    public InlineBuilder(InlineMenuRepository inlineMenuRepository) {
        this.inlineMenuRepository = inlineMenuRepository;
    }

    /**
     * Формирование меню с переходами
     *
     * @param menu InlineMenu
     * @return InlineKeyboardMarkup
     */
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
            if (telegramAnswer.getButton() != null &&
                    telegramAnswer.getTagCallback() != null &&
                    telegramAnswer.getButton().length() > 0 &&
                    telegramAnswer.getTagCallback().length() > 0) {
                keyboardMarkup.addRow(new InlineKeyboardButton(telegramAnswer.getButton()).callbackData(telegramAnswer.getTagCallback()));
            }
        });
        log.debug("InlineKeyboardMarkup: {}", keyboardMarkup);
        return keyboardMarkup;
    }

    /**
     * Формирование меню отчёта
     *
     * @param reportSetByAnimalType LinkedHashSet
     * @return InlineKeyboardMarkup
     */
    public InlineKeyboardMarkup getInlineMenuReport(LinkedHashSet<AnimalReportType> reportSetByAnimalType) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        reportSetByAnimalType.forEach(telegramAnswer -> {
            if (telegramAnswer.getButton() != null &&
                    telegramAnswer.getTagCallback() != null &&
                    telegramAnswer.getButton().length() > 0 &&
                    telegramAnswer.getTagCallback().length() > 0) {
                keyboardMarkup.addRow(new InlineKeyboardButton(telegramAnswer.getButton()).callbackData(telegramAnswer.getTagCallback()));
            }
        });
        keyboardMarkup.addRow(new InlineKeyboardButton("EXIT").callbackData("EXIT"));
        log.debug("InlineKeyboardMarkup: {}", keyboardMarkup);
        return keyboardMarkup;
    }

    /**
     * Добавление кнопки выход.
     *
     * @return new InlineKeyboardMarkup()
     */
    public InlineKeyboardMarkup getInlineMenuExit() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(new InlineKeyboardButton("EXIT").callbackData("EXIT"));
        return keyboardMarkup;
    }
}
