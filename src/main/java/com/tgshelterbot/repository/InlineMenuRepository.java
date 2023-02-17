package com.tgshelterbot.repository;

import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InlineMenuRepository extends JpaRepository<InlineMenu, Long> {

    /**
     * Найти по language, shelter, question
     *
     * @param language Long
     * @param shelter  Long
     * @param question String
     * @return Optional
     */
    Optional<InlineMenu> findFirstByLanguageIdAndShelterIdAndQuestion(Long language, Long shelter, String question);

    /**
     * Найти по language, shelter, tag
     *
     * @param language Long
     * @param shelter  Long
     * @param tag      String
     * @return Optional
     */
    Optional<InlineMenu> findFirstByLanguageIdAndShelterIdAndTagCallback(Long language, Long shelter, String tag);

    /**
     * Найти по stateId
     *
     * @param stateId UserState
     * @return List<InlineMenu>
     */
    List<InlineMenu> findAllByStateId(UserState stateId);

    Optional<InlineMenu> findFirstByTagCallback(String tag);
}
