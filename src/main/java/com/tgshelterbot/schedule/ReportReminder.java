package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.repository.AnimalReportDataRepository;
import com.tgshelterbot.repository.AnimalReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ReportReminder {
    private final AnimalReportDataRepository reportDataRepository;
    private final AnimalReportRepository animalReportRepository;
    private final TelegramBot bot;
    private Collection<AnimalReportData> reports;

    @Scheduled(fixedDelay = 60_000)//12 hours
    public void remind() {
        reports = reportDataRepository.findAllByState(AnimalReportStateEnum.CREATED)
                .stream()
                .filter(e -> e.getDtCreate().getHour() + OffsetDateTime.now().getHour() > 1)
                .collect(Collectors.toList());
        String message = "Отсутствует отчёт: %s%n";
        if (!reports.isEmpty()) {
            Map<Long, String> userReportMAp = reports.stream()
                    .collect(Collectors.toMap(AnimalReportData::getTelegramUser,
                            e -> String.format(message, e.getReportType().getName()),
                            (v, v1) -> v + v1));
            userReportMAp.forEach((k, v) -> bot.execute(new SendMessage(k, v)));
            log.info("Message; {}", userReportMAp);
        }
    }
}
