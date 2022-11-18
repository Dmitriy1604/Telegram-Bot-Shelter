package com.tgshelterbot.repository;

import com.tgshelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Найти по Ид
     *
     * @param aLong Long
     * @return Optional
     */
    Optional<User> findByTelegramId(Long aLong);
}
