package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.AnimalReportStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class AnimalReportDataDto implements Serializable {
    private final Long id;
    private final String reportText;
    private final byte[] reportDataFile;
    private final OffsetDateTime dtCreate;
    private final OffsetDateTime dtAccept;
    private final Long tgMessageId;
    private final String localFileName;
    private final AnimalReportStateEnum state;
}
