package com.tgshelterbot.crm;

import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.model.User;

public interface UserService {
    /**
     * Получение телеграм ИД пользователя
     * @param update событие на сервере
     * @return idUser
     */
    Long getIdUser(Update update);

    User update(User user);

    /**
     * Поиск или создание пользователя
     * @param id Long
     * @return пользователь
     */
    User findUserOrCreate(Long id);
}
