package com.tgshelterbot.mapper;

import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.dto.InlineMenuDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InlineMenuMapper {
    public InlineMenuDto toDto(InlineMenu inlineMenu);

    public InlineMenu toEntity(InlineMenuDto dto);
}
