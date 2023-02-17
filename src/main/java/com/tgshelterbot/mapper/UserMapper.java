package com.tgshelterbot.mapper;

import com.tgshelterbot.model.User;
import com.tgshelterbot.model.dto.UserDto;
import org.mapstruct.Mapper;
/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
     User toEntity(UserDto dto);

     UserDto toDto(User user);
}
