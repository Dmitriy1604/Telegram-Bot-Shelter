package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.UserCrudMapper;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.dto.UserDtoCrud;
import com.tgshelterbot.model.dto.UserDtoCrudSerialized;
import com.tgshelterbot.repository.*;
import com.tgshelterbot.service.UserCRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCRUDServiceImpl implements UserCRUDService {
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final ShelterRepository shelterRepository;
    private final UserStateRepository stateRepository;
    private final AnimalReportRepository reportRepository;
    private final UserCrudMapper userMapper;

    @Override
    public UserDtoCrudSerialized getUser(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public Collection<UserDtoCrudSerialized> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDtoCrudSerialized updateUser(UserDtoCrud userDto) {
        languageRepository.findById(userDto.getLanguage())
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
        shelterRepository.findById(userDto.getShelter())
                .orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
    }

    @Override
    public UserDtoCrudSerialized deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
        return userMapper.toDto(user);
    }
}
