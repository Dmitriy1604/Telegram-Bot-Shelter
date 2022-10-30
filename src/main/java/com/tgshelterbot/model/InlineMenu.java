package com.tgshelterbot.model;

import lombok.*;
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

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_id_next")
    private Long stateIdNext;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "special_state")
    @Enumerated(EnumType.STRING)
    private UserStateSpecial userStateSpecial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }

    public String getTagCallback() {
        return tagCallback;
    }

    public void setTagCallback(String tagCallback) {
        this.tagCallback = tagCallback;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getStateIdNext() {
        return stateIdNext;
    }

    public void setStateIdNext(Long stateIdNext) {
        this.stateIdNext = stateIdNext;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

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