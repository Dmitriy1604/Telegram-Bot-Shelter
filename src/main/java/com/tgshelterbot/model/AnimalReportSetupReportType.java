package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "animal_report_setup_report_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AnimalReportSetupReportType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "setup_id", nullable = false)
    private Long setup;

    @Column(name = "report_type_id", nullable = false)
    private Long reportTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnimalReportSetupReportType that = (AnimalReportSetupReportType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}