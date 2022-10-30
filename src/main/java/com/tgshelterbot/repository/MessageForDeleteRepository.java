package com.tgshelterbot.repository;

import com.tgshelterbot.model.MessageForDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageForDeleteRepository extends JpaRepository<MessageForDelete, Long> {
}
