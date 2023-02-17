package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_state")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "tag_special")
    @Enumerated(EnumType.STRING)
    private UserStateSpecial tagSpecial;

    @Column(name = "shelter_id")
    private Long shelterId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserState userState = (UserState) o;
        return id != null && Objects.equals(id, userState.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}