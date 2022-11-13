package com.tgshelterbot.service;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.AnimalSimpleDto;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;

public interface AnimalService {
    List<AnimalDto> getAll();

    List<AnimalSimpleDto> getAllSimpleAnimal();

    AnimalDto create(AnimalDto dto);

    AnimalDto read(Long id);

    AnimalDto update(Long id, AnimalDto dto);

    AnimalDto delete(Long id);

    Collection<AnimalDto> findAllBySateInTest(Animal.AnimalStateEnum stateEnum);

    AnimalDto extendPeriod(Long id, Animal.TimeFrame timeFrame);
}
