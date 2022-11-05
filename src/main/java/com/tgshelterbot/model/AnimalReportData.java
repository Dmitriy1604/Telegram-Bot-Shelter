package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "animal_report_data")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AnimalReportData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "telegram_user_id", nullable = false, referencedColumnName = "telegram_id")
    @ToString.Exclude
    private User telegramUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_report_id", nullable = false)
    @ToString.Exclude
    private AnimalReport animalReport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_type_id", nullable = false)
    @ToString.Exclude
    private AnimalReportType reportType;

    @Column(name = "report_text")
    @Type(type = "org.hibernate.type.TextType")
    private String reportText;

    @Column(name = "report_data_file")
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
}