package com.tgshelterbot.mapper;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.model.dto.UserDto;
import com.tgshelterbot.model.dto.UserStateDto;
import org.springframework.stereotype.Component;

@Component
public class MapperDTO {

    public ShelterDto toDto(Shelter shelter) {
        return new ShelterDto(shelter.getId(), shelter.getName());
    }

    public Shelter toEntity(ShelterDto dto) {
        Shelter shelter = new Shelter();
        shelter.setName(dto.getName());
        return shelter;
    }

    public UserStateDto toDto(UserState userState) {
        return new UserStateDto(userState.getId(),
                userState.getName(),
                userState.getTagSpecial(),
                userState.getShelterId());
    }

    public UserState toEntity(UserStateDto userStateDto) {
        UserState state = new UserState();
        state.setId(userStateDto.getId());
        state.setName(userStateDto.getName().trim());
        state.setTagSpecial(userStateDto.getTagSpecial());
        state.setShelterId(userStateDto.getShelterId());
        return state;
    }

    public AnimalDto toDto(Animal entity) {
        return new AnimalDto(entity);
    }


    public User toEntity(UserDto dto) {
        User out = new User();
        if (dto.getUserStateDto() != null) {
            out.setStateId(toEntity(dto.getUserStateDto()));
        }
        if (dto.getLanguage() != null) {
            out.setLanguage(dto.getLanguage());
        }
        if (dto.getShelter() != null) {
            out.setShelter(dto.getShelter());
        }
        if (dto.getTelegramId() != null) {
            out.setTelegramId(dto.getTelegramId());
        }
        return out;
    }

    public Animal toEntity(AnimalDto dto) {
        Animal animal = new Animal();
        if (dto.getId() != null) {
            animal.setId(dto.getId());
        }
        if (dto.getAnimalTypeId() != null) {
            animal.setAnimalTypeId(dto.getAnimalTypeId());
        }
        if (dto.getShelterId() != null) {
            animal.setShelterId(dto.getShelterId());
        }
        if (dto.getUserId() != null) {
            animal.setUserId(dto.getUserId());
        }
        if (dto.getDtCreate() != null) {
            animal.setDtCreate(dto.getDtCreate());
        }
        if (dto.getDtStartTest() != null) {
            animal.setDtStartTest(dto.getDtStartTest());
        }
        if (dto.getDaysForTest() != null) {
            animal.setDaysForTest(dto.getDaysForTest());
        }
        if (dto.getName() != null) {
            animal.setName(dto.getName());
        }
        if (dto.getState() != null) {
            animal.setState(dto.getState());
        }
        return animal;
    }

    public UserDto toDto(User user) {
        return new UserDto(user);
    }

}