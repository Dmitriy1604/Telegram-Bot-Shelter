package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "animal_report")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AnimalReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    @ToString.Exclude
    private Animal animal;

    @Column(name = "dt_create")
    private OffsetDateTime dtCreate;

    @Column(name = "dt_accept")
    private OffsetDateTime dtAccept;

    @OneToMany(mappedBy = "animalReport")
    @ToString.Exclude
    private Set<AnimalReportData> animalReportData = new LinkedHashSet<>();

    @Column(name = "state", columnDefinition = "report_state not null")
    private AnimalReportStateEnum state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnimalReport that = (AnimalReport) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}