package com.tgshelterbot.service.impl;

import com.tgshelterbot.dto.UserStateDto;
import com.tgshelterbot.dto.mapper.DtoMapper;
import com.tgshelterbot.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStateService {

    private final UserStateRepository stateRepository;
    private final DtoMapper dtoMapper;
    private final static String LOG_SAMPLE = "\n::: Service method {} was invoked :: result: {}";

    public Collection<UserStateDto> getAllUserStates () {
        Collection<UserStateDto> all = stateRepository.findAll()
                .stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug(LOG_SAMPLE, "getAll", all);
        return all;
    }

    public UserStateDto getUserState (Long id) {
        UserStateDto userStateDto =
                stateRepository.findById(id).map(dtoMapper::toDto).orElseThrow(EntityNotFoundException::new);
        log.debug(LOG_SAMPLE, "getUserState", userStateDto);
        return userStateDto;
    }

    public UserStateDto saveOrUpdate (UserStateDto userStateDto) {
        UserStateDto state = dtoMapper.toDto(stateRepository.save(dtoMapper.toEntity(userStateDto)));
        log.debug(LOG_SAMPLE, "saveOrUpdate", state.toString());
        return state;
    }

    public void removeUserState (Long id) {
        log.debug(LOG_SAMPLE, "removeUserState", Void.TYPE.toString());
        stateRepository.deleteById(id);
    }
}
