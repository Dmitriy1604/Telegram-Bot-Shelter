package com.tgshelterbot.mapper;

import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.UserStateDto;
import org.mapstruct.Mapper;

/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface UserStateMapper {
     UserStateDto toDto(UserState userState);

     UserState toEntity(UserStateDto userStateDto);
}
