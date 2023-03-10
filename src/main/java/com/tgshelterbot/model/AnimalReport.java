package com.tgshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

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

    @Column(name = "user_id")
    private Long userId;

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

    @Column(name = "message")
    private String message;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "animalReport")
    @JsonIgnore
    private List<AnimalReportData> animalReportDataList;

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