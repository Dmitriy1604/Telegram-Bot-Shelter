package com.tgshelterbot.service.impl;

import com.tgshelterbot.exception.ShelterIsNotExistsException;
import com.tgshelterbot.mapper.ShelterMapper;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository repository;
    private final ShelterMapper shelterMapper;

    @Override
    public List<ShelterDto> getAll() {
        return repository.findAll().stream().map(shelterMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ShelterDto create(ShelterDto dto) {
        Shelter shelter = shelterMapper.toEntity(dto);
        return shelterMapper.toDto(repository.save(shelter));
    }

    @Override
    public ShelterDto read(Long id) {
        return shelterMapper.toDto(repository.findById(id).orElseThrow(ShelterIsNotExistsException::new));
    }

    @Override
    public ShelterDto update(Long id, ShelterDto dto) {
        Shelter shelter = repository.findById(id).orElseThrow(ShelterIsNotExistsException::new);
        shelter.setId(id);
        shelter.setName(dto.getName());

        return shelterMapper.toDto(repository.save(shelter));
    }

    @Override
    public ShelterDto delete(Long id) {
        Shelter shelter = repository.findById(id).orElseThrow(ShelterIsNotExistsException::new);
        repository.delete(shelter);
        return shelterMapper.toDto(shelter);
    }

}
