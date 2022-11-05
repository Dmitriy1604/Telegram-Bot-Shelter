package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long> {
}