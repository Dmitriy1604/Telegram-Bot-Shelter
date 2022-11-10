package com.tgshelterbot.crm;

import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.model.User;

public interface UserService {
    Long getIdUser(Update update);

    User update(User user);

    User findUserOrCreate(Long id);
}
