package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.AnimalReportSetupDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AnimalTypeDto implements Serializable {
    private final Long id;
    private final String name;
    private final AnimalReportSetupDto animalReportSetup;
    private final Integer daysForTest;
    private final Set<AnimalDto> animals;
}
