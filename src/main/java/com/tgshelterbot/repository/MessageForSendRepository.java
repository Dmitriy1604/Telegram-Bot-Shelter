package com.tgshelterbot.repository;

import com.tgshelterbot.model.MessageForSend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageForSendRepository extends JpaRepository<MessageForSend, Long> {

    List<MessageForSend> findAllByDtNeedSendBeforeAndDeletedFalse(OffsetDateTime offsetDateTime);
}