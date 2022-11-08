package com.tgshelterbot.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnimalSimpleDto implements Serializable {
    private final Long id;
    private final String animal;
    private final String shelter;
}
