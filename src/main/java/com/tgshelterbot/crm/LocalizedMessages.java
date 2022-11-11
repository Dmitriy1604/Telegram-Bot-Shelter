package com.tgshelterbot.crm;

import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.model.User;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class LocalizedMessages {
    private final String resource = "message";

    @SneakyThrows
    public String get(String name, User user) {
        Locale locale;
        if (user.getLanguage() == 2L) {
            locale = new Locale("en");
            return ResourceBundle.getBundle(resource, locale).getString(name);
        }
        locale = new Locale("ru");
        return ResourceBundle.getBundle(resource, locale).getString(name);
    }

    public String get(String name, Update update) {
        if (update.message() != null && update.message().from() != null) {
            String languageCode = update.message().from().languageCode();
            Locale locale = new Locale(languageCode, languageCode);
            return ResourceBundle.getBundle(resource, locale).getString(name);
        }
        Locale locale = new Locale("ru");
        return ResourceBundle.getBundle(resource, locale).getString(name);
    }

    public String get(String name) {
        Locale locale = new Locale("ru");
        return ResourceBundle.getBundle(resource, locale).getString(name);
    }
}
