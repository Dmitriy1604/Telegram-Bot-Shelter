package com.tgshelterbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ShelterDto implements Serializable {
    private final Long id;
    @NotEmpty
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShelterDto dto = (ShelterDto) o;
        return Objects.equals(id, dto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
