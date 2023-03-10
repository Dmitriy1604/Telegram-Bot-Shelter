package com.tgshelterbot.mapper;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalDto;
import org.mapstruct.Mapper;

/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface AnimalMapper {
    AnimalDto toDto(Animal animal);

    Animal toEntity(AnimalDto animalDto);
}
