package com.tgshelterbot.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "message_for_send")
public class MessageForSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "text", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    @Column(name = "dt_need_send", nullable = false)
    private OffsetDateTime dtNeedSend;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getDtNeedSend() {
        return dtNeedSend;
    }

    public void setDtNeedSend(OffsetDateTime dtNeedSend) {
        this.dtNeedSend = dtNeedSend;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}