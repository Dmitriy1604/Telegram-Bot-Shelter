package com.tgshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "animal_report_data")
@Getter
@Setter
@RequiredArgsConstructor
@org.hibernate.annotations.TypeDef(name = "report_state", typeClass = PostgreSQLEnumType.class)
public class AnimalReportData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "telegram_user_id")
    private Long telegramUser;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "animal_report_id", nullable = false)
    private AnimalReport animalReport;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "report_type_id", nullable = false)
    private AnimalReportType reportType;

    @Column(name = "report_text")
    @Type(type = "org.hibernate.type.TextType")
    private String reportText;

    @Column(name = "report_data_file")
    @JsonIgnore
    private byte[] reportDataFile;

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime dtCreate;

    @Column(name = "dt_accept")
    private OffsetDateTime dtAccept;

    @Column(name = "tg_message_id")
    private Long tgMessageId;

    @Column(name = "local_file_name")
    @Type(type = "org.hibernate.type.TextType")
    private String localFileName;

    @Column(name = "state", columnDefinition = "report_state")
    @Enumerated(EnumType.STRING)
    @Type(type = "report_state")
    private AnimalReportStateEnum state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnimalReportData that = (AnimalReportData) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AnimalReportData{" +
                "id=" + id +
                ", telegramUser=" + telegramUser +
                '}';
    }
}