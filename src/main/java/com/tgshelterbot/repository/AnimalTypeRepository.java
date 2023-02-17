package com.tgshelterbot.repository;

import com.tgshelterbot.model.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {
}