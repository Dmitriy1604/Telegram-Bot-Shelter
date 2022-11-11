package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long> {
    Optional<AnimalReport> findFirstByStateAndAnimalOrderById(AnimalReportStateEnum stateEnum, Long animalId);

    Optional<AnimalReport> findFirstByAnimalAndDtCreateAfter(Long animalId, OffsetDateTime dtCreate);

    LinkedHashSet<AnimalReport> findAllByAnimalAndDtCreateAfterOrderByDtCreate(Long animalId, OffsetDateTime dtCreate);

    List<AnimalReport> findAllByStateAndDtCreateBefore(AnimalReportStateEnum state, OffsetDateTime dtCreate);
}