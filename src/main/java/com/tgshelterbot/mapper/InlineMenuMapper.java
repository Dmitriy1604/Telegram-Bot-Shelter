package com.tgshelterbot.mapper;

import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.dto.InlineMenuDto;
import org.mapstruct.Mapper;
/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface InlineMenuMapper {
     InlineMenuDto toDto(InlineMenu inlineMenu);

     InlineMenu toEntity(InlineMenuDto dto);
}
