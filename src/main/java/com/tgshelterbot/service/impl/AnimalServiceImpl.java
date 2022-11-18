package com.tgshelterbot.service.impl;

import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.LocalizedMessages;
import com.tgshelterbot.crm.MessageSender;
import com.tgshelterbot.mapper.AnimalMapper;
import com.tgshelterbot.mapper.AnimalSimpleMapper;
import com.tgshelterbot.mapper.SummarizedReportMapper;
import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.AnimalType;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.dto.*;
import com.tgshelterbot.repository.*;
import com.tgshelterbot.service.AnimalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository repository;
    private final AnimalTypeRepository animalTypeRepository;
    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final AnimalMapper animalMapper;
    private final AnimalSimpleMapper animalSimpleMapper;
    private final LocalizedMessages locale;
    private final MessageSender messageSender;
    private final SummarizedReportMapper summarizedReportMapper;

    @Override
    public List<AnimalDto> getAll() {
        return repository.findAll().stream().map(animalMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AnimalSimpleDto> getAllSimpleAnimal() {
//        return repository.findAll().stream().map(animal -> animalSimpleMapper.toDto(animal, shelterRepository.findById(animal.getShelterId()).get())).collect(Collectors.toList());
        List<AnimalsSimple> simple = repository.findAllAnimalsSimple();
        return animalSimpleMapper.toDtos(simple);
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
        shelterRepository.findById(dto.getShelterId()).orElseThrow(EntityNotFoundException::new);
        userRepository.findByTelegramId(dto.getUserId()).orElseThrow(EntityNotFoundException::new);
        AnimalType animalType = animalTypeRepository.findById(dto.getId()).orElseThrow(EntityNotFoundException::new);
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

    @Override
    public Collection<AnimalDto> findAllBySateInTest(Animal.AnimalStateEnum stateEnum) {
        return repository.findAllByUserIdNotNullAndState(stateEnum)
                .stream()
                .map(animalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnimalDto changePeriod(Long id, Animal.TimeFrame timeFrame) {
        Animal animal = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("animal doesn't exist"));
        User user = userRepository.findByTelegramId(animal.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        if (timeFrame.getPeriod() == 0) {
            animal.setDaysForTest(0);
            messageSender.sendMessage(new SendMessage(user.getTelegramId(),
                    String.format(locale.get("congrats.message"), animal.getName())), user);
        } else {
            animal.setDaysForTest(animal.getDaysForTest() + timeFrame.getPeriod());
            messageSender.sendMessage(new SendMessage(user.getTelegramId(),
                    String.format(locale.get("period.extended"),
                            animal.getName(),
                            timeFrame.getPeriod(),
                            animal.getDaysForTest())), user);
        }
        return animalMapper.toDto(repository.save(animal));
    }

    public Collection<SummarizedReportDto> getAllSummarized(Long id) {
        Collection<SummarizedReport> summarizedReports = repository.getAllAndSummarizeReport(id);
        return summarizedReports.stream()
                .map(summarizedReportMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
