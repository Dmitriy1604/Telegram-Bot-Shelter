package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.tgshelterbot.crm.ReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportRejectSchedule {

    private final Logger log = LoggerFactory.getLogger(DeleteMessageSchedule.class);
    private final TelegramBot bot;
    private final ReportService reportService;

    // 1 сек 2 мин 9 часа каждого дня
    @Scheduled(cron = "1 2 9 * * *")
    public void runReportRejectSchedule() {
        log.info("ReportRejectSchedule running");
        reportService.runClosureScheduler();
    }
}
