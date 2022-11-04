package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.ShelterDto;

import java.util.List;

public interface ShelterService {
    List<ShelterDto> getAll();
    ShelterDto create(ShelterDto dto);

    ShelterDto read(Long id);

    ShelterDto update(Long id, ShelterDto dto);

    ShelterDto delete(Long id);
}
