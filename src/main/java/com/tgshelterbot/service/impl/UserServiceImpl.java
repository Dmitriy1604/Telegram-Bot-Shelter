package com.tgshelterbot.service.impl;

import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.UserRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserStateRepository userStateRepository;

    public UserServiceImpl(UserRepository userRepository, UserStateRepository userStateRepository) {
        this.userRepository = userRepository;
        this.userStateRepository = userStateRepository;
    }


    @Override
    public User update(User user) {
        logger.info("Set user state: " + user.getStateId());
        return userRepository.save(user);
    }

    @Override
    public User findUserOrCreate(Long id) {
        return userRepository.findByTelegramId(id).orElseGet(() -> create(id));
    }

    public User create(Long id) {
        User user = new User();
        user.setTelegramId(id);
        user.setStateId(userStateRepository.findFirstByTagSpecial(UserStateSpecial.SELECT_SHELTER).orElse(null));
        user.setLanguage(1L); /*TODO доделаять языки*/
        if (user.getDtCreate() == null) {
            user.setDtCreate(OffsetDateTime.now());
        }
        logger.info("New user was created: " + user);
        return userRepository.save(user);
    }

    private Long getUserId(Update update) {
        return update.message().chat().id();
    }
}
