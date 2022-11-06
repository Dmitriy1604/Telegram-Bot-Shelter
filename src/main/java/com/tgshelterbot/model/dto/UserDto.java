package com.tgshelterbot.model.dto;

import com.tgshelterbot.model.User;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    @NotNull
    private Long telegramId;
    private Long language;
    private Long shelter;
    private UserStateDto userStateDto;

    public UserDto() {
    }

    public UserDto(User user) {
        if (user.getTelegramId() != null) {
            this.telegramId = user.getTelegramId();
        }
        if (user.getLanguage() != null) {
            this.language = user.getLanguage();
        }
        if (user.getShelter() != null) {
            this.shelter = user.getShelter();
        }
        if (user.getStateId() != null) {
            this.userStateDto = new UserStateDto(); //TODO FIX THIS
        }
    }
}
