package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.AnimalReportStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class AnimalReportDto implements Serializable {
    private Long id;
    private Long userId;
    private Long animal;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtAccept;
    private AnimalReportStateEnum state;
    private List<AnimalReportDataDto> animalReportDataDtos;
}
