package com.tgshelterbot.model.dto;

import java.time.OffsetDateTime;

public interface UserDtoCrudSerialized {
    Long getId();

    Long getTelegramId();

    Long getLanguage();

    Long getShelter();

    Long getUserStateId();

    Long getReportId();

    String getPhone();

    String getName();

    String getInfo();

    OffsetDateTime getDtCreate();

    void setId(Long id);

    void setTelegramId(Long telegramId);

    void setLanguage(Long language);

    void setShelter(Long shelter);

    void setUserStateId(Long userStateId);

    void setReportId(Long reportId);

    void setPhone(String phone);

    void setName(String name);

    void setInfo(String info);

    void setDtCreate(OffsetDateTime dtCreate);
}
