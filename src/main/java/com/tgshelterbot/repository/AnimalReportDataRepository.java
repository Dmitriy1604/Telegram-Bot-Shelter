package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface AnimalReportDataRepository extends JpaRepository<AnimalReportData, Long> {
    /**
     * Найти animal_report_data по reportId и reportTypeId
     *
     * @param reportId     Long
     * @param reportTypeId Long
     * @return Optional<AnimalReportData>
     */
    @Query(value = "select * from animal_report_data where animal_report_id=? and report_type_id=? limit 1;",
            nativeQuery = true)
    Optional<AnimalReportData> findCurrentReport(Long reportId, Long reportTypeId);

    /**
     * Найти всех по статусу
     *
     * @param stateEnum AnimalReportStateEnum
     * @return Collection<AnimalReportData>
     */
    Collection<AnimalReportData> findAllByState(AnimalReportStateEnum stateEnum);

    /**
     * Обновить статус с CREATED на REJECTED по ИД
     *
     * @param animalReportId long
     */
    @Modifying
    @Query(value = "update animal_report_data set state='REJECTED' where animal_report_id=? and state='CREATED'",
            nativeQuery = true)
    void updateSetCloseOldReport(long animalReportId);
}