package com.tgshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.tgshelterbot.crm.TelegramFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramFacade botFacade;
    private final TelegramBot bot;

    public TelegramBotUpdatesListener(TelegramFacade botFacade, TelegramBot bot) {
        this.botFacade = botFacade;
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            try {
                botFacade.processUpdate(update);
            } catch (Exception exception) {
                logger.info("exception: {}", exception.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
