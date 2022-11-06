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
<<<<<<<< HEAD:src/main/java/com/tgshelterbot/model/dto/UserStateDto.java
    @NotNull
========
>>>>>>>> origin/feature-andrey:src/main/java/com/tgshelterbot/dto/UserStateDto.java
    @NotBlank
    private String name;
    private UserStateSpecial tagSpecial;
    @NotNull
    private Long shelterId;
}
