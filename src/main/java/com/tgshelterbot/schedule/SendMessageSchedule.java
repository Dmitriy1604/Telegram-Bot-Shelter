package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.MessageForSend;
import com.tgshelterbot.repository.MessageForSendRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SendMessageSchedule {
    private final Logger log = LoggerFactory.getLogger(DeleteMessageSchedule.class);
    private final TelegramBot bot;
    private final MessageForSendRepository repository;

    @Scheduled(fixedDelay = 60_000)
    public void sendMessage() {
        List<MessageForSend> sendMessages = repository.findAllByDtNeedSendBeforeAndDeletedFalse(OffsetDateTime.now());
        for (MessageForSend sendMessage : sendMessages) {
            SendMessage send = new SendMessage(sendMessage.getUserId(), sendMessage.getText());
            sendMessage.setDeleted(true);
            repository.save(sendMessage);
            bot.execute(send);
        }
    }
}
