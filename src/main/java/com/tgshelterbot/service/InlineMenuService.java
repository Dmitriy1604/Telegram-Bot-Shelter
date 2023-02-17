package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.InlineMenuDto;

import java.util.Collection;

public interface InlineMenuService {
    /**
     * Сохранить новый пункт меню
     *
     * @param inlineMenuDto InlineMenuDto
     * @return InlineMenuDto
     */
    InlineMenuDto saveInlineMenu(InlineMenuDto inlineMenuDto);

    /**
     * Получить пункт меню по Ид
     *
     * @param id Long
     * @return InlineMenuDto
     */
    InlineMenuDto getInlineMenu(Long id);

    /**
     * Получить все пункты меню
     *
     * @return Collection<InlineMenuDto>
     */
    Collection<InlineMenuDto> getAllInlineMenu();

    /**
     * Обновить пункт меню
     *
     * @param inlineMenuDto InlineMenuDto
     * @return InlineMenuDto
     */
    InlineMenuDto updateInlineMenu(InlineMenuDto inlineMenuDto);

    /**
     * Удалить пункт меню
     *
     * @param id Long
     * @return InlineMenuDto
     */
    InlineMenuDto deleteInlineMenu(Long id);
}
