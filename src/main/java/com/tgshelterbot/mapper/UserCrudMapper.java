package com.tgshelterbot.mapper;

import com.tgshelterbot.model.User;
import com.tgshelterbot.model.dto.UserDtoCrud;
import org.mapstruct.Mapper;

/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface UserCrudMapper {
    User toEntity(UserDtoCrud dto);

    UserDtoCrud toDto(User user);
}
