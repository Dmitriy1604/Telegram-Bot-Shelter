package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long> {
    List<AnimalReport> findAllByDtCreateBetweenOrderById(PageRequest pageRequest, OffsetDateTime dtStart, OffsetDateTime dtEnd);

    List<AnimalReport> findAllByDtCreateBetweenAndStateOrderById(PageRequest pageRequest, OffsetDateTime dtStart, OffsetDateTime dtEnd, AnimalReportStateEnum stateEnum);

    Optional<AnimalReport> findFirstByStateAndAnimalOrderById(AnimalReportStateEnum stateEnum, Long animalId);

    Optional<AnimalReport> findFirstByAnimalAndDtCreateAfter(Long animalId, OffsetDateTime dtCreate);

    LinkedHashSet<AnimalReport> findAllByAnimalAndDtCreateAfterOrderByDtCreate(Long animalId, OffsetDateTime dtCreate);

    List<AnimalReport> findAllByStateAndDtCreateBefore(AnimalReportStateEnum state, OffsetDateTime dtCreate);

    @Query(value = "SELECT * FROM animal_report WHERE dt_create > now() - INTERVAL '1 MINUTE'", nativeQuery = true)
    Collection<AnimalReport> findAllForToday();
}