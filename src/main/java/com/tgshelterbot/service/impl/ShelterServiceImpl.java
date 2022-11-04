package com.tgshelterbot.service.impl;

import com.tgshelterbot.exception.ShelterIsNotExistsException;
import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.service.ShelterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository repository;
    private final MapperDTO mapperDTO;

    @Override
    public List<ShelterDto> getAll() {
        return repository.findAll().stream().map(mapperDTO::toDto).collect(Collectors.toList());
    }

    @Override
    public ShelterDto create(ShelterDto dto) {
        Shelter shelter = mapperDTO.toEntity(dto);
        return mapperDTO.toDto(repository.save(shelter));
    }

    @Override
    public ShelterDto read(Long id) {
        return mapperDTO.toDto(repository.findById(id).orElseThrow(ShelterIsNotExistsException::new));
    }

    @Override
    public ShelterDto update(Long id, ShelterDto dto) {
        Shelter shelter = repository.findById(id).orElseThrow(ShelterIsNotExistsException::new);
        shelter.setId(id);
        shelter.setName(dto.getName());

        return mapperDTO.toDto(repository.save(shelter));
    }

    @Override
    public ShelterDto delete(Long id) {
        Shelter shelter = repository.findById(id).orElseThrow(ShelterIsNotExistsException::new);
        repository.delete(shelter);
        return mapperDTO.toDto(shelter);
    }

}
