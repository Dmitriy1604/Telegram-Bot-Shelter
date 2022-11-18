package com.tgshelterbot.repository;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalsSimple;
import com.tgshelterbot.model.dto.SummarizedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    /**
     * Найти всех животных у юзера
     *
     * @param id Long
     * @return List
     */
    List<Animal> findAnimalsByUserId(Long id);

    /**
     * Комбинированный список животных с названием приюта
     *
     * @return List
     */
    @Query(value = "select a.id as id, a.name as animal, s.name as shelter  from animals a inner join shelters s on a.shelter_id = s.id order by a.id",
            nativeQuery = true)
    List<AnimalsSimple> findAllAnimalsSimple();

    /**
     * Найти всех по статусу
     *
     * @param stateEnum AnimalStateEnum
     * @return Collection
     */
    Collection<Animal> findAllByUserIdNotNullAndState(Animal.AnimalStateEnum stateEnum);

    /**
     * Найти всех с окончившимся тестовым периодом
     *
     * @return Collection
     */
    @Query(value = "SELECT * FROM animals WHERE date_trunc('DAY',dt_start_test+days_for_test * INTERVAL'1  DAY') <= " +
            "cast(now() AS  date)", nativeQuery = true)
    Collection<Animal> findAllWithEndPeriod();

    /**
     * Общий отчёт по животному
     *
     * @param id Long
     * @return Collection
     */
    @Query(value =
            "SELECT animal_id AS animalId, count(ar.state) AS count, ar.state AS state, a.days_for_test AS daysForTest\n" +
                    "FROM animal_report ar\n" +
                    "         INNER JOIN animals a\n" +
                    "                    ON ar.animal_id = a.id\n" +
                    "GROUP BY ar.animal_id, a.days_for_test, ar.state\n" +
                    "HAVING ar.animal_id = ?", nativeQuery = true)
    Collection<SummarizedReport> getAllAndSummarizeReport(Long id);
}