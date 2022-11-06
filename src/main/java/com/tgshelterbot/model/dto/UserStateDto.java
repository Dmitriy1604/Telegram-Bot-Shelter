package com.tgshelterbot.model.dto;

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
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private UserStateSpecial tagSpecial;
    @NotNull
    private Long shelterId;
}
