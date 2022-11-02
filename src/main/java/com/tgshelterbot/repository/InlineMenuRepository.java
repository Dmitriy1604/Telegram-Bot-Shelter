package com.tgshelterbot.repository;

import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InlineMenuRepository extends JpaRepository<InlineMenu, Long> {

    Optional<InlineMenu> findFirstByLanguageIdAndShelterIdAndQuestion(Long language, Long shelter, String question);
    Optional<InlineMenu> findFirstByLanguageIdAndShelterIdAndTagCallback(Long language, Long shelter, String tag);
    List<InlineMenu> findAllByStateId(UserState stateId);
    Optional<InlineMenu> findFirstByTagCallback(String tag);
}
