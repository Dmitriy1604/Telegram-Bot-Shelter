package com.tgshelterbot.model.dto;

public interface SummarizedReport {
    Long getAnimalId();

    Long getCount();

    String getState();

    Integer getDaysForTest();
}
