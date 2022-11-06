package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.dto.UserStateDto;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.UserStateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserStateServiceImpl implements UserStateService {

    private final UserStateRepository stateRepository;
    private final MapperDTO dtoMapper;
    private final String LOG_SAMPLE = "Service method {} was invoked with parameter :: {}";
    private final String NOT_FOUND = "User state doesn't exist";
    private final String NOT_ACCEPTABLE = "User state field should be assigned by admins only!";
    private final String FORBIDDEN = "This state creation is not allowed!";

    @Override
    public Collection<UserStateDto> getAllUserStates() {
        Collection<UserStateDto> all = stateRepository.findAll()
                .stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug(LOG_SAMPLE, "getAll", Void.TYPE);
        return all;
    }

    @Override
    public UserStateDto getUserState(Long id) {
        log.debug(LOG_SAMPLE, "getUserState", id);
        return stateRepository.findById(id)
                .map(dtoMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
    }

    @Override
    public UserStateDto save(UserStateDto userStateDto) {
        if (Objects.nonNull(userStateDto.getTagSpecial())) {
            throw new IllegalArgumentException(NOT_ACCEPTABLE);
        } else if (stateRepository.findById(userStateDto.getId()).isPresent() ||
                userStateDto.getId() < 0 ||
                userStateDto.getShelterId() < 1) {
            throw new EntityExistsException(FORBIDDEN);
        }
        log.debug(LOG_SAMPLE, "save", userStateDto);
        return dtoMapper.toDto(stateRepository.save(dtoMapper.toEntity(userStateDto)));
    }

    @Override
    public UserStateDto update(UserStateDto userStateDto) {
        if (Objects.nonNull(userStateDto.getTagSpecial())) {
            throw new IllegalArgumentException(NOT_ACCEPTABLE);
        } else if (stateRepository.findById(userStateDto.getId()).isEmpty() ||
                userStateDto.getId() < 0 ||
                userStateDto.getShelterId() < 1) {
            throw new EntityExistsException(FORBIDDEN);
        }
        log.debug(LOG_SAMPLE, "update", userStateDto.toString());
        return dtoMapper.toDto(stateRepository.save(dtoMapper.toEntity(userStateDto)));
    }

    @Override
    public void removeUserState(Long id) {
        log.debug(LOG_SAMPLE, "removeUserState", Void.TYPE);
        stateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(FORBIDDEN));
        stateRepository.deleteById(id);
    }
}
