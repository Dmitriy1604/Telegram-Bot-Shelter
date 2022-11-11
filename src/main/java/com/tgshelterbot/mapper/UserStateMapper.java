package com.tgshelterbot.mapper;

import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.UserStateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserStateMapper {
    public UserStateDto toDto(UserState userState);

    public UserState toEntity(UserStateDto userStateDto);
}
