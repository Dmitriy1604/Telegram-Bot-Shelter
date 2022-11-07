package com.tgshelterbot.mapper;

import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.dto.InlineMenuDto;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.model.dto.UserStateDto;
import org.springframework.stereotype.Component;

@Component
public class MapperDTO {

    public ShelterDto toDto(Shelter shelter) {
        return new ShelterDto(shelter.getId(), shelter.getName());
    }

    public Shelter toEntity(ShelterDto dto) {
        Shelter shelter = new Shelter();
        shelter.setName(dto.getName());
        return shelter;
    }

    public UserStateDto toDto(UserState userState) {
        return new UserStateDto(userState.getId(),
                userState.getName(),
                userState.getTagSpecial(),
                userState.getShelterId());
    }

    public UserState toEntity(UserStateDto userStateDto) {
        UserState state = new UserState();
        state.setId(userStateDto.getId());
        state.setName(userStateDto.getName().trim());
        state.setTagSpecial(userStateDto.getTagSpecial());
        state.setShelterId(userStateDto.getShelterId());
        return state;
    }

    public InlineMenuDto toDto(InlineMenu inlineMenu) {
        return new InlineMenuDto(inlineMenu.getId(),
                inlineMenu.getLanguageId(),
                inlineMenu.getShelterId(),
                inlineMenu.getTagCallback(),
                inlineMenu.getQuestion(),
                inlineMenu.getAnswer(),
                inlineMenu.getButton(),
                inlineMenu.getStateId(),
                inlineMenu.getStateIdNext(),
                inlineMenu.getPriority());
    }

    public InlineMenu toEntity(InlineMenuDto dto) {
        InlineMenu inlineMenu = new InlineMenu();
        inlineMenu.setId(dto.getId());
        inlineMenu.setLanguageId(dto.getLanguageId());
        inlineMenu.setShelterId(dto.getShelterId());
        inlineMenu.setTagCallback(dto.getTagCallback());
        inlineMenu.setQuestion(dto.getQuestion());
        inlineMenu.setAnswer(dto.getAnswer());
        inlineMenu.setButton(dto.getButton());
        inlineMenu.setStateId(dto.getStateId());
        inlineMenu.setStateIdNext(dto.getStateIdNext());
        inlineMenu.setPriority(inlineMenu.getPriority());
        return inlineMenu;
    }
}
