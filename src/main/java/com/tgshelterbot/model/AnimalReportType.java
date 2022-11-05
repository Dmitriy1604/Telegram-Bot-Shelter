package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "animal_report_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AnimalReportType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "button", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String button;

    @Column(name = "tag_callback")
    @Type(type = "org.hibernate.type.TextType")
    private String tagCallback;

    @Column(name = "text_is_good_content")
    @Type(type = "org.hibernate.type.TextType")
    private String textIsGoodContent;

    @Column(name = "text_is_bad_content")
    @Type(type = "org.hibernate.type.TextType")
    private String textIsBadContent;

    @Column(name = "is_text", nullable = false)
    private Boolean isText = false;

    @Column(name = "is_file", nullable = false)
    private Boolean isFile = false;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "language_id", nullable = false)
    @ToString.Exclude
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shelter_id", nullable = false)
    @ToString.Exclude
    private Shelter shelter;

    @OneToMany(mappedBy = "reportType")
    @ToString.Exclude
    private Set<AnimalReportData> animalReportData = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reportType")
    @ToString.Exclude
    private Set<AnimalReportSetup> animalReportSetups = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnimalReportType that = (AnimalReportType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}