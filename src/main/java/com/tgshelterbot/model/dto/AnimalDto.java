package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.Animal;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class AnimalDto implements Serializable {
    private Long id;
    private Long animalTypeId;
    private Long userId;
    private Long shelterId;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtStartTest;
    private Integer daysForTest;
    private String name;
    private Animal.AnimalStateEnum state;
}
