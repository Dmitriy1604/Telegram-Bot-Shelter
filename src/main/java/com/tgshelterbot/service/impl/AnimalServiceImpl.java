package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.repository.AnimalRepository;
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
    private final MapperDTO mapperDTO;

    @Override
    public List<AnimalDto> getAll() {
        return repository.findAll().stream().map(mapperDTO::toDto).collect(Collectors.toList());
    }

    @Override
    public AnimalDto create(AnimalDto dto) {
        //Создается животное без пользователя и всегда с дефолтным статусом IN_SHELTER"
        dto.setUserId(null);
        dto.setDtCreate(null);
        dto.setDaysForTest(null);
        dto.setDtCreate(OffsetDateTime.now());
        dto.setState(Animal.AnimalStateEnum.IN_SHELTER);
        Animal animal = mapperDTO.toEntity(dto);
        Animal save = repository.save(animal);
        return mapperDTO.toDto(save);
    }

    @Override
    public AnimalDto read(Long id) {
        return mapperDTO.toDto(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public AnimalDto update(Long id, AnimalDto dto) {
        Animal animal = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        animal.setId(id);
        animal.setName(dto.getName());

        return mapperDTO.toDto(repository.save(animal));
    }

    @Override
    public AnimalDto delete(Long id) {
        Animal animal = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        repository.delete(animal);
        return mapperDTO.toDto(animal);
    }
}
