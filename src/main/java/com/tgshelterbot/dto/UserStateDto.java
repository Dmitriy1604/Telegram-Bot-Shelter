package com.tgshelterbot.dto;

import com.tgshelterbot.model.UserStateSpecial;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserStateDto {
    private Long id;
    @NotBlank
    private String name;
    private UserStateSpecial stateSpecial;
    @NotNull
    private Long shelterId;
}
