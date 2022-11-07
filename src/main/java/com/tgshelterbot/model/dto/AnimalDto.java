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

    public AnimalDto() {
    }

    public AnimalDto(Animal animal) {
        if (animal.getId() != null) {
            this.id = animal.getId();
        }
        if (animal.getAnimalTypeId() != null) {
            this.animalTypeId = animal.getAnimalTypeId();
        }
        if (animal.getShelterId() != null){
            this.shelterId = animal.getShelterId();
        }
        if (animal.getUserId() != null) {
            this.userId = animal.getUserId();
        }
        if (animal.getDtCreate() != null) {
            this.dtCreate = animal.getDtCreate();
        }
        if (animal.getDtStartTest() != null) {
            this.dtStartTest = animal.getDtStartTest();
        }
        if (animal.getDaysForTest() != null) {
            this.daysForTest = animal.getDaysForTest();
        }
        if (animal.getName() != null) {
            this.name = animal.getName();
        }
        if (animal.getState() != null) {
            this.state = animal.getState();
        }
    }
}
