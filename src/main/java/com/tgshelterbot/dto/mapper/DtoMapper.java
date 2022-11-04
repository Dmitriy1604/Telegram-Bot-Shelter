package com.tgshelterbot.dto.mapper;

import com.tgshelterbot.dto.UserStateDto;
import com.tgshelterbot.model.UserState;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public UserStateDto toDto (UserState userState) {
        return new UserStateDto(userState.getId(),
                userState.getName(),
                userState.getTagSpecial(),
                userState.getShelterId());
    }

    public UserState toEntity (UserStateDto userStateDto) {
        UserState state = new UserState();
        state.setName(userStateDto.getName());
        state.setTagSpecial(userStateDto.getStateSpecial());
        state.setShelterId(userStateDto.getShelterId());
        return state;
    }
}
