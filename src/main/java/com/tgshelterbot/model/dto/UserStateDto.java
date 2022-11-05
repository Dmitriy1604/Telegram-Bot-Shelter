package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserStateDto implements Serializable {
    private Long id;
    @NotBlank
    private String name;
    private UserStateSpecial tagSpecial;
    @NotNull
    private Long shelterId;

    public UserStateDto() {
    }

    public UserStateDto(UserState userState) {
        if (userState.getId() != null) {
            this.id = userState.getId();
        }
        if (userState.getName() != null) {
            this.name = userState.getName();
        }
        if (userState.getTagSpecial() != null) {
            this.tagSpecial = userState.getTagSpecial();
        }
        if (userState.getShelterId() != null) {
            this.shelterId = userState.getShelterId();
        }
    }
}
