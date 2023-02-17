package com.tgshelterbot.mapper;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.AnimalSimpleDto;
import com.tgshelterbot.model.dto.AnimalsSimple;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface AnimalSimpleMapper {
    @Mapping(source = "animal.id", target = "id")
    @Mapping(source = "animal.name", target = "animal")
    @Mapping(source = "shelter.name", target = "shelter")
    AnimalSimpleDto toDto(Animal animal, Shelter shelter);


    List<AnimalSimpleDto> toDtos(List<AnimalsSimple> animalsSimple);
}
