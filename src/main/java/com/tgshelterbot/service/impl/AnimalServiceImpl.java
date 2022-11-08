package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.AnimalMapper;
import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.AnimalType;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.repository.AnimalRepository;
import com.tgshelterbot.repository.AnimalTypeRepository;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.service.AnimalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository repository;
    private final AnimalTypeRepository animalTypeRepository;
    private final ShelterRepository shelterRepository;
    private final AnimalMapper animalMapper;

    @Override
    public List<AnimalDto> getAll() {
        return repository.findAll().stream().map(animalMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AnimalDto create(AnimalDto dto) {
        //Создается животное без пользователя и всегда с дефолтным статусом IN_SHELTER
        dto.setUserId(null);
        dto.setDtCreate(null);
        dto.setDaysForTest(null);
        dto.setDtStartTest(null);
        dto.setDtCreate(OffsetDateTime.now());
        dto.setState(Animal.AnimalStateEnum.IN_SHELTER);
        Animal animal = animalMapper.toEntity(dto);
        Animal save = repository.save(animal);
        return animalMapper.toDto(save);
    }

    @Override
    public AnimalDto read(Long id) {
        return animalMapper.toDto(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public AnimalDto update(Long id, AnimalDto dto) {
        Animal original = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        AnimalType animalType = animalTypeRepository.findById(dto.getId()).orElseThrow(EntityNotFoundException::new);
        shelterRepository.findById(dto.getShelterId()).orElseThrow(EntityNotFoundException::new);

        Animal update = animalMapper.toEntity(dto);
        update.setId(id);
        update.setDtCreate(original.getDtCreate());

        if (dto.getUserId() != null && original.getUserId() == null) {
            update.setDtStartTest(OffsetDateTime.now());
            update.setState(Animal.AnimalStateEnum.IN_TEST);
            update.setDaysForTest(animalType.getDaysForTest());
        }

        Animal save = repository.save(update);
        return animalMapper.toDto(save);
    }

    @Override
    public AnimalDto delete(Long id) {
        Animal animal = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        repository.delete(animal);
        return animalMapper.toDto(animal);
    }
}
