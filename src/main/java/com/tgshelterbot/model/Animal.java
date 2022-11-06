package com.tgshelterbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "animals")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@org.hibernate.annotations.TypeDef(name = "animal_state", typeClass = PostgreSQLEnumType.class)
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "animal_type_id", nullable = false)
    private Long animalTypeId;

    @Column(name = "shelter_id", nullable = false)
    private Long shelterId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime dtCreate;

    @Column(name = "dt_start_test")
    private OffsetDateTime dtStartTest;

    @Column(name = "days_for_test")
    private Integer daysForTest;
    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "state", columnDefinition = "animal_state")
    @Enumerated(EnumType.STRING)
    @Type(type = "animal_state")
    private AnimalStateEnum state;

    public enum AnimalStateEnum {
        IN_SHELTER, IN_TEST, HAPPY_END
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Animal animal = (Animal) o;
        return id != null && Objects.equals(id, animal.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}