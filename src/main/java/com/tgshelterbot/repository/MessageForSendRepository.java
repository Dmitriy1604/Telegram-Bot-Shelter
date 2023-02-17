package com.tgshelterbot.repository;

import com.tgshelterbot.model.MessageForSend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageForSendRepository extends JpaRepository<MessageForSend, Long> {

    /**
     * Найти все по дате и статусу не удалён
     *
     * @param offsetDateTime OffsetDateTime
     * @return List<MessageForSend>
     */
    List<MessageForSend> findAllByDtNeedSendBeforeAndDeletedFalse(OffsetDateTime offsetDateTime);
}