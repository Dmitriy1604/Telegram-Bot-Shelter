package com.tgshelterbot.service;

import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.model.User;

public interface UserService {
    User update(User user);
    User findUserOrCreate(Long id);
}
