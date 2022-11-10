package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AnimalReportDataRepository extends JpaRepository<AnimalReportData, Long> {
    @Query(value = "select * from animal_report_data where animal_report_id=? and report_type_id=? limit 1;", nativeQuery = true)
    Optional<AnimalReportData> findCurrentReport(Long reportId, Long reportTypeId);

    @Modifying
    @Query(value = "update animal_report_data set state='REJECTED' where animal_report_id=? and state='CREATED'", nativeQuery = true)
    void updateSetCloseOldReport(long animalReportId);
}