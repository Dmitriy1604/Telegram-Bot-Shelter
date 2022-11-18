package com.tgshelterbot.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDtoCrud implements UserDtoCrudSerialized {
    private Long id;
    private Long telegramId;
    private Long language;
    private Long shelter;
    private Long userStateId;
    private Long reportId;
    private String phone;
    private String name;
    private String info;
    private OffsetDateTime dtCreate;
}
