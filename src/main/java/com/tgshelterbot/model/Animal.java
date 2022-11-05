package com.tgshelterbot.model;

import lombok.*;
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
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_type_id", nullable = false)
    @ToString.Exclude
    private AnimalType animalType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shelter_id", nullable = false)
    @ToString.Exclude
    private Shelter shelter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "telegram_id")
    @ToString.Exclude
    private User user;

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