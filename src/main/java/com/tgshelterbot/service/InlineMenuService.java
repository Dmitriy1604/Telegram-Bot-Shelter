package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.InlineMenuDto;

import java.util.Collection;

public interface InlineMenuService {
    InlineMenuDto saveInlineMenu(InlineMenuDto inlineMenuDto);

    InlineMenuDto getInlineMenu(Long id);

    Collection<InlineMenuDto> getAllInlineMenu();

    InlineMenuDto updateInlineMenu(InlineMenuDto inlineMenuDto);

    InlineMenuDto deleteInlineMenu(Long id);
}
