package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReportData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalReportDataRepository extends JpaRepository<AnimalReportData, Long> {
}