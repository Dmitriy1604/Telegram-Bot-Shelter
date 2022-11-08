package com.tgshelterbot.repository;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalsSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findAnimalsByUserId(Long id);
    @Query(value = "select a.id as id, a.name as animal, s.name as shelter  from animals a inner join shelters s on a.shelter_id = s.id order by a.id",nativeQuery = true)
    List<AnimalsSimple> findAllAnimalsSimple();
}