package com.tgshelterbot.mapper;

import com.tgshelterbot.model.User;
import com.tgshelterbot.model.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toEntity(UserDto dto);

    public UserDto toDto(User user);
}
