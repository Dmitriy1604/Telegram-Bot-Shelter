package com.tgshelterbot.repository;

import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    Optional<UserState> findFirstByShelterIdAndTagSpecial(Long aLong, UserStateSpecial tag);

    /**
     * Найти по спец тэгу
     *
     * @param tag UserStateSpecial
     * @return Optional
     */
    Optional<UserState> findFirstByTagSpecial(UserStateSpecial tag);
}
