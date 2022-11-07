package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.UserStateDto;

import java.util.Collection;

public interface UserStateService {
    Collection<UserStateDto> getAllUserStates();

    UserStateDto getUserState(Long id);

    UserStateDto save(UserStateDto userStateDto);

    UserStateDto update(UserStateDto userStateDto);

    UserStateDto removeUserState(Long id);
}
