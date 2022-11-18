package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShelterMenu {
    private final Logger log = LoggerFactory.getLogger(ShelterMenu.class);
    private final ShelterRepository shelterRepository;

    /**
     * Построение меню из списка приютов
     * @param user User
     * @return new SendMessage с меню
     */
    public SendMessage getShelterMenu(@NotNull User user) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<Shelter> shelterList = shelterRepository.findAll();
        shelterList.forEach(language -> {
                    keyboardMarkup.addRow(new InlineKeyboardButton(language.getName())
                            .callbackData(language.getId().toString()));
                }
        );

        log.debug("ShelterMenu: {}", keyboardMarkup);
        return new SendMessage(user.getTelegramId(), "Выберите приют").replyMarkup(keyboardMarkup);
    }
}
