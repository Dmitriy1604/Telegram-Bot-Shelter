package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalReportSetup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalReportSetupRepository extends JpaRepository<AnimalReportSetup, Long> {
}