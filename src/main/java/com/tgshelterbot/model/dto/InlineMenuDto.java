package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.UserState;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class InlineMenuDto {
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull
    private Long languageId;
    @NotNull
    private Long shelterId;
    @NotNull
    @NotBlank
    private String tagCallback;
    @NotNull
    @NotBlank
    private String question;
    @NotNull
    @NotBlank
    private String answer;
    @NotNull
    @NotBlank
    private String button;
    private UserState stateId;
    private UserState stateIdNext;
    @NotNull
    private Integer priority;
}
