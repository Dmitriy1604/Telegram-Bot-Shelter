package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "animal_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AnimalType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_report_setup_id")
    @ToString.Exclude
    private AnimalReportSetup animalReportSetup;

    @Column(name = "days_for_test")
    private Integer daysForTest;

    @OneToMany(mappedBy = "animalType")
    @ToString.Exclude
    private Set<Animal> animals = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnimalType that = (AnimalType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}