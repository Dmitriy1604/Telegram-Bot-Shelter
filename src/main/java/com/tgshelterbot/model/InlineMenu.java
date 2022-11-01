package com.tgshelterbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inline_menu")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class InlineMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "shelter_id", nullable = false)
    private Long shelterId;

    @Column(name = "tag_callback")
    @Type(type = "org.hibernate.type.TextType")
    private String tagCallback;

    @Column(name = "question")
    @Type(type = "org.hibernate.type.TextType")
    private String question;

    @Column(name = "answer")
    @Type(type = "org.hibernate.type.TextType")
    private String answer;

    @Column(name = "button")
    @Type(type = "org.hibernate.type.TextType")
    private String button;

    @JoinColumn(name = "state_id")
    @OneToOne
    private UserState stateId;

    @JoinColumn(name = "state_id_next")
    @OneToOne
    private UserState stateIdNext;

    @Column(name = "priority")
    private Integer priority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InlineMenu that = (InlineMenu) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}