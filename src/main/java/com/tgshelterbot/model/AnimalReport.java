package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

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
@org.hibernate.annotations.TypeDef(name = "report_state", typeClass = PostgreSQLEnumType.class)
public class AnimalReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "animal_id")
    private Long animal;

    @Column(name = "dt_create")
    private OffsetDateTime dtCreate;

    @Column(name = "dt_accept")
    private OffsetDateTime dtAccept;

    @Column(name = "state", columnDefinition = "report_state")
    @Enumerated(EnumType.STRING)
    @Type(type = "report_state")
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