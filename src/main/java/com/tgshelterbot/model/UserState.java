package com.tgshelterbot.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_state")
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "tag_special")
    @Type(type = "org.hibernate.type.TextType")
    private String tagSpecial;

    @Column(name = "shelter_id")
    private Long shelterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagSpecial() {
        return tagSpecial;
    }

    public void setTagSpecial(String tagSpecial) {
        this.tagSpecial = tagSpecial;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }

}