package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.AnimalReportDataDto;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class AnimalReportDto implements Serializable {
    private final Long id;
    private final AnimalDto animal;
    private final OffsetDateTime dtCreate;
    private final OffsetDateTime dtAccept;
    private final Set<AnimalReportDataDto> animalReportData;
    private final AnimalReportStateEnum state;
}
