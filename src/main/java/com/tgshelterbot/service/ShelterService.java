package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.ShelterDto;

import java.util.List;

public interface ShelterService {
    /**
     * Получить все приюты
     *
     * @return List<ShelterDto>
     */
    List<ShelterDto> getAll();

    /**
     * Создать новый приют
     *
     * @param dto ShelterDto
     * @return ShelterDto
     */
    ShelterDto create(ShelterDto dto);

    /**
     * Найти приют по ИД
     *
     * @param id Long
     * @return ShelterDto
     */
    ShelterDto read(Long id);

    /**
     * Обновить приют
     *
     * @param id  Long
     * @param dto ShelterDto
     * @return ShelterDto
     */
    ShelterDto update(Long id, ShelterDto dto);

    /**
     * Удалить приют
     *
     * @param id Long
     * @return ShelterDto
     */
    ShelterDto delete(Long id);
}
