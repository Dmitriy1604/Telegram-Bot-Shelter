package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long> {
    Optional<AnimalReport> findFirstByStateAndAnimalOrderById(AnimalReportStateEnum stateEnum, Long animalId);
}