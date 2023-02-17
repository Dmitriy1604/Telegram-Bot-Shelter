package com.tgshelterbot.service;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.AnimalSimpleDto;
import com.tgshelterbot.model.dto.SummarizedReportDto;

import java.util.Collection;
import java.util.List;

public interface AnimalService {
    /**
     * Получение все животных
     *
     * @return List<AnimalDto>
     */
    List<AnimalDto> getAll();

    /**
     * Получение все животных упрощёный вид
     *
     * @return List<AnimalSimpleDto>
     */
    List<AnimalSimpleDto> getAllSimpleAnimal();

    /**
     * Создание животного
     *
     * @param dto AnimalDto
     * @return AnimalDto
     */
    AnimalDto create(AnimalDto dto);

    /**
     * Получение животного по Ид
     *
     * @param id Long
     * @return AnimalDto
     */
    AnimalDto read(Long id);

    /**
     * Обновить животное
     *
     * @param id  Long
     * @param dto AnimalDto
     * @return AnimalDto
     */
    AnimalDto update(Long id, AnimalDto dto);

    /**
     * Удалить животное
     *
     * @param id Long
     * @return AnimalDto
     */
    AnimalDto delete(Long id);

    /**
     * Поиск всех кто на испытательном сроке
     *
     * @param stateEnum AnimalStateEnum
     * @return Collection<AnimalDto>
     */
    Collection<AnimalDto> findAllBySateInTest(Animal.AnimalStateEnum stateEnum);

    /**
     * Изменить тестовый период
     *
     * @param id        Long
     * @param timeFrame TimeFrame
     * @return AnimalDto
     */
    AnimalDto changePeriod(Long id, Animal.TimeFrame timeFrame);

    /**
     * Получение обобщеного отчета по тестовому периоду по животному
     *
     * @param id Long
     * @return Collection<SummarizedReportDto>
     */
    Collection<SummarizedReportDto> getAllSummarized(Long id);
}
