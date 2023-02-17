package com.tgshelterbot.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tgshelterbot.model.AnimalReportStateEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class AnimalReportDataDto implements Serializable {
    private final Long id;
    @NotNull
    private final Long telegramUser;
    private final String reportText;
    private final byte[] reportDataFile;
    private final OffsetDateTime dtCreate;
    private final OffsetDateTime dtAccept;
    private final Long tgMessageId;
    @JsonIgnore
    private final String localFileName;
    private final AnimalReportStateEnum state;
}