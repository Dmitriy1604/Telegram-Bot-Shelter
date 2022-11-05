package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.dto.ShelterDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class AnimalReportTypeDto implements Serializable {
    private final Long id;
    private final String name;
    private final String button;
    private final String tagCallback;
    private final String textIsGoodContent;
    private final String textIsBadContent;
    private final Boolean isText;
    private final Boolean isFile;
    private final Integer priority;
    private final ShelterDto shelter;
}
