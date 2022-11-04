package com.tgshelterbot.mapper;

import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.ShelterDto;
import org.springframework.stereotype.Component;

@Component
public class MapperDTO {

    public ShelterDto toDto(Shelter shelter) {
        return new ShelterDto(shelter.getId(), shelter.getName());
    }

    public Shelter toEntity(ShelterDto dto) {
        Shelter shelter = new Shelter();
        shelter.setName(dto.getName());
        return shelter;
    }

}
