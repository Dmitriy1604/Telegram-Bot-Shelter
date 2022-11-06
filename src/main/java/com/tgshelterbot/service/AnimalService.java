package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.ShelterDto;

import java.util.List;

public interface AnimalService {
    List<AnimalDto> getAll();

    AnimalDto create(AnimalDto dto);

    AnimalDto read(Long id);

    AnimalDto update(Long id, AnimalDto dto);

    AnimalDto delete(Long id);
}
