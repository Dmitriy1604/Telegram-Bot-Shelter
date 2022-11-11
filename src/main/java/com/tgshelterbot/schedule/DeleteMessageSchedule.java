package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.tgshelterbot.model.MessageForDelete;
import com.tgshelterbot.repository.MessageForDeleteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteMessageSchedule {

    private final Logger log = LoggerFactory.getLogger(DeleteMessageSchedule.class);
    private final TelegramBot bot;
    private final MessageForDeleteRepository repository;

    public DeleteMessageSchedule(TelegramBot bot, MessageForDeleteRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 600_000_000)
    public void deleteMessage() {
        List<MessageForDelete> deleteList = repository.findAll();

        deleteList.forEach(messageForDelete -> {
            DeleteMessage deleteMessage = new DeleteMessage(messageForDelete.getTelegramUser(), messageForDelete.getMessageId());
            bot.execute(deleteMessage);
        });
        if (!deleteList.isEmpty()) {
            log.debug("DeleteMessageSchedule: list {}", deleteList);
            repository.deleteAll();
        }
    }
}
