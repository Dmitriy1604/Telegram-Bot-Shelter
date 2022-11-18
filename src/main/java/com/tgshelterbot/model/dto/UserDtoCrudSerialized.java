package com.tgshelterbot.model.dto;

public interface UserDtoCrudSerialized {
    Long getId();

    Long getTelegramId();

    Long getLanguage();

    Long getShelter();

    Long getReportId();

    String getPhone();

    String getName();

    String getInfo();

    java.time.OffsetDateTime getDtCreate();

    void setId(Long id);

    void setTelegramId(Long telegramId);

    void setLanguage(Long language);

    void setShelter(Long shelter);

    void setReportId(Long reportId);

    void setPhone(String phone);

    void setName(String name);

    void setInfo(String info);

    void setDtCreate(java.time.OffsetDateTime dtCreate);
}
