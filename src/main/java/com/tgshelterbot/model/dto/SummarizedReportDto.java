package com.tgshelterbot.model.dto;

import lombok.Data;

@Data
public class SummarizedReportDto {

    private Long animalId;
    private Long count;
    private String state;
    private Integer daysForTest;
}
