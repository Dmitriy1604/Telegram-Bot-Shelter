package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long> {
    /**
     * Найти по статуту и Ид животного
     *
     * @param stateEnum AnimalReportStateEnum
     * @param animalId  Long
     * @return Optional
     */
    Optional<AnimalReport> findFirstByStateAndAnimalOrderById(AnimalReportStateEnum stateEnum, Long animalId);

    Optional<AnimalReport> findFirstByAnimalAndDtCreateAfter(Long animalId, OffsetDateTime dtCreate);

    /**
     * Найти все отчёты по животному после заданной даты
     *
     * @param animalId Long
     * @param dtCreate OffsetDateTime
     * @return LinkedHashSet<AnimalReport>
     */
    LinkedHashSet<AnimalReport> findAllByAnimalAndDtCreateAfterOrderByDtCreate(Long animalId, OffsetDateTime dtCreate);

    /**
     * Найти все отчёты по  статусу до заданной даты
     *
     * @param state    AnimalReportStateEnum
     * @param dtCreate OffsetDateTime
     * @return List<AnimalReport>
     */
    List<AnimalReport> findAllByStateAndDtCreateBefore(AnimalReportStateEnum state, OffsetDateTime dtCreate);

    /**
     * Найти все отчеты за сегодня
     *
     * @return Collection<AnimalReport>
     */
    @Query(value = "SELECT *\n" + "FROM animal_report\n" + "WHERE CAST(dt_create AS DATE) > CAST(now() AS DATE) - 1",
            nativeQuery = true)
    Collection<AnimalReport> findAllForToday();

    /**
     * Найти все отчёты за 2 дня
     *
     * @return Collection
     */
    @Query(value = "SELECT * FROM animal_report WHERE dt_create > now() - INTERVAL '2 DAYS'", nativeQuery = true)
    Collection<AnimalReport> findAllForTwoDays();
}