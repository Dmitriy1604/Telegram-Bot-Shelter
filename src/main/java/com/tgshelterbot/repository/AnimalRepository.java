package com.tgshelterbot.repository;

import com.tgshelterbot.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findAnimalsByUserId(Long id);
}