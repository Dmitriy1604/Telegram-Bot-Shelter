package com.tgshelterbot.service.impl;

import com.tgshelterbot.dto.mapper.DtoMapper;
import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.UserStateDto;
import com.tgshelterbot.repository.AnimalRepository;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStateService {

    private final UserStateRepository repository;
    private final ShelterRepository shelterRepository;
    private final MapperDTO mapperDTO;
    private final static String LOG_SAMPLE = "\n::: Service method {} was invoked :: result: {}";

    public List<UserStateDto> getAll() {
        return repository.findAll().stream().map(mapperDTO::toDto).collect(Collectors.toList());
    }


    public UserStateDto create(UserStateDto dto) {
        dto.setId(null);
        dto.setTagSpecial(null);
        shelterRepository.findById(dto.getShelterId()).orElseThrow(EntityNotFoundException::new);
        UserState save = repository.save(mapperDTO.toEntity(dto));
        return mapperDTO.toDto(save);
    }

    public UserStateDto read(Long id) {
        return mapperDTO.toDto(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserStateDto update(Long id, UserStateDto dto) {
        if (id < 0){
            throw new EntityNotFoundException("Запрещено редактировать сервисные статусы");
        }
        UserState state = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        state.setId(id);
        state.setName(dto.getName());
        return mapperDTO.toDto(repository.save(state));
    }

    public UserStateDto delete(Long id) {
        if (id < 0){
            throw new EntityNotFoundException("Запрещено редактировать сервисные статусы");
        }
        UserState state = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        repository.delete(state);
        return mapperDTO.toDto(state);
    }
}
