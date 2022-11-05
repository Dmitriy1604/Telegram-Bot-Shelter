package com.tgshelterbot.mapper;

import com.tgshelterbot.model.*;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.model.dto.UserDto;
import com.tgshelterbot.model.dto.UserStateDto;
import com.tgshelterbot.repository.AnimalRepository;
import com.tgshelterbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapperDTO {
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
//    private final AnimalRepository animalRepository;

    public ShelterDto toDto(Shelter shelter) {
        return new ShelterDto(shelter.getId(), shelter.getName());
    }

    public AnimalDto toDto(Animal entity) {
        return new AnimalDto(entity);
    }

    public Shelter toEntity(ShelterDto dto) {
        Shelter shelter = new Shelter();
        shelter.setName(dto.getName());
        return shelter;
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

    public UserState toEntity(UserStateDto userStateDto) {
        UserState state = new UserState();
        state.setName(userStateDto.getName());
        state.setTagSpecial(userStateDto.getTagSpecial());
        state.setShelterId(userStateDto.getShelterId());
        return state;
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


    /*TODo  подумать над юзером, по идее ничего кроме id не даем по нему*/
    public UserStateDto toDto(UserState userState) {
        return new UserStateDto(userState);
    }


}
