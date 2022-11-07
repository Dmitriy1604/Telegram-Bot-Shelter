package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.InlineMenuDto;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.InlineMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InlineMenuServiceImpl implements InlineMenuService {
    private final InlineMenuRepository menuRepository;
    private final UserStateRepository stateRepository;
    private final MapperDTO mapper;
    private final String NOT_ACCEPTABLE = "Arguments is not acceptable.";// 406 will have been thrown
    private final String NOT_FOUND = "Menu doesn't exist";//404 will have been thrown
    private final String LOG_SAMPLE = "Service method {} was invoked with parameter :: {}";

    public InlineMenuDto saveInlineMenu(InlineMenuDto inlineMenuDto) {
        log.debug(LOG_SAMPLE, "saveInlineMenu", inlineMenuDto);
        Set<Long> stateIds = stateRepository.findAll().stream().map(UserState::getId).collect(Collectors.toSet());
        if (!stateIds.contains(inlineMenuDto.getStateIdNext().getId()) || inlineMenuDto.getStateId().getId() < 0) {
            throw new IllegalArgumentException(NOT_ACCEPTABLE);// 406 will have been thrown
        } else if (menuRepository.findById(inlineMenuDto.getId()).isPresent()) {
            String FORBIDDEN = "This menu already exist";//403 will have been thrown
            throw new EntityExistsException(FORBIDDEN);
        }
        inlineMenuDto.setId(null);
        return mapper.toDto(menuRepository.save(mapper.toEntity(inlineMenuDto)));
    }

    public InlineMenuDto getInlineMenu(Long id) {
        log.debug(LOG_SAMPLE, "getInlineMenu", id);
        //404 will have been thrown
        return menuRepository.findById(id).map(mapper::toDto).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
    }

    public Collection<InlineMenuDto> getAllInlineMenu() {
        log.debug(LOG_SAMPLE, "getAllInlineMenu", Void.TYPE);
        return menuRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public InlineMenuDto updateInlineMenu(InlineMenuDto inlineMenuDto) {
        log.debug(LOG_SAMPLE, "updateInlineMenu", inlineMenuDto);
        Set<Long> stateIds = stateRepository.findAll().stream().map(UserState::getId).collect(Collectors.toSet());
        if (!stateIds.contains(inlineMenuDto.getStateIdNext().getId()) || inlineMenuDto.getStateId().getId() < 0) {
            throw new IllegalArgumentException(NOT_ACCEPTABLE);
        } else if (menuRepository.findById(inlineMenuDto.getId()).isEmpty()) {
            throw new EntityNotFoundException(NOT_FOUND);
        }
        return mapper.toDto(menuRepository.save(mapper.toEntity(inlineMenuDto)));
    }

    public InlineMenuDto deleteInlineMenu(Long id) {
        log.debug(LOG_SAMPLE, "deleteInlineMenu", id);
        InlineMenuDto dto =
                mapper.toDto(menuRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND)));
        menuRepository.deleteById(id);
        return dto;
    }
}
