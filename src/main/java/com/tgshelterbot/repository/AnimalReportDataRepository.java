package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.AnimalReportStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface AnimalReportDataRepository extends JpaRepository<AnimalReportData, Long> {
    @Query(value = "select * from animal_report_data where animal_report_id=? and report_type_id=? limit 1;", nativeQuery = true)
    Optional<AnimalReportData> findCurrentReport(Long reportId, Long reportTypeId);

    Collection<AnimalReportData>findAllByState(AnimalReportStateEnum stateEnum);
}