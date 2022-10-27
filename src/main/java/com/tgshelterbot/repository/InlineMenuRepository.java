package com.tgshelterbot.repository;

import com.tgshelterbot.model.InlineMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InlineMenuRepository extends JpaRepository<InlineMenu, Long> {

    Optional<InlineMenu> findFirstByLanguageIdAndAndShelterIdAndQuestion(Long language, Long shelter, String question);
    List<InlineMenu> findAllByStateId(Long stateId);
    Optional<InlineMenu> findFirstByTagCallback(String tag);
}
