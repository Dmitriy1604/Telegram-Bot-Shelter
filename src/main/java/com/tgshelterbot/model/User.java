package com.tgshelterbot.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @Column(name = "language_id")
    private Long language;

    @Column(name = "shelter_id")
    private Long shelter;

    @Column(name = "state_id")
    private Long stateId;

    //Используем для возврата в прошлое меню, если были специальные статусы
    @Column(name = "previous_state_id")
    private Long previousStateId;

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "last_response_statemenu_id")
    private Long lastResponseStatemenuId;

    @Column(name = "phone")
    @Type(type = "org.hibernate.type.TextType")
    private String phone;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "info")
    @Type(type = "org.hibernate.type.TextType")
    private String info;

    @Column(name = "dt_create")
    private OffsetDateTime dtCreate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}