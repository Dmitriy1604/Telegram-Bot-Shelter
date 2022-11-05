package com.tgshelterbot.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AnimalReportSetupDto implements Serializable {
    private final Long id;
    private final AnimalReportTypeDto reportType;
    private final Set<AnimalTypeDto> animalTypes;
}
