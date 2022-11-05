package com.tgshelterbot.service.impl;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.AnimalReportType;
import com.tgshelterbot.model.User;
import com.tgshelterbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportDataRepository animalReportDataRepository;
    private final AnimalReportSetupRepository animalReportSetupRepository;
    private final AnimalReportSetupReportTypeRepository animalReportSetupReportTypeRepository;
    private final AnimalReportTypeRepository animalReportTypeRepository;
    private final AnimalRepository animalRepository;

    public Animal getAnimal(User user){
        List<Animal> animalsByUserId = animalRepository.findAnimalsByUserId(user.getTelegramId());
        return animalsByUserId.stream()
                .filter(animal -> animal.getState().equals(Animal.AnimalStateEnum.IN_TEST))
                .findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public LinkedHashSet<AnimalReportType> getAnimalReportType(){
        return null;
    }
}
