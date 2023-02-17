package com.tgshelterbot.mapper;

import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.ShelterDto;
import org.mapstruct.Mapper;
/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface ShelterMapper {
    ShelterDto toDto(Shelter shelter);
    Shelter toEntity(ShelterDto shelterDto);
}
