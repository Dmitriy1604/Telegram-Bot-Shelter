package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.AnimalReportDataRepository;
import com.tgshelterbot.repository.AnimalReportRepository;
import com.tgshelterbot.repository.AnimalReportTypeRepository;
import com.tgshelterbot.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ReportReminder {
    private final AnimalReportDataRepository reportDataRepository;
    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportTypeRepository animalReportTypeRepository;

    private final AnimalRepository animalRepository;
    private final TelegramBot bot;

    @Value("${telegram.bot.support.chat}")
    private String chatId;

    /**
     * Каждые 6 часов напоминает о необходимости сдать все или недостающие отчёты.
     */
    @Scheduled(fixedDelay = 21_600_000_000_000L)//6 hours
    public void remindForUsers() {
        Collection<Animal> animals = animalRepository.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST);
        for (Animal animal : animals) {
            //берём все отчеты за сегодня
            Collection<AnimalReport> animalReports = animalReportRepository.findAllForToday()
                    .stream()
                    .filter(e -> animal.getUserId().equals(e.getUserId()))   // фильтруем по юзер ид
                    .collect(Collectors.toList());
            if (animalReports.isEmpty()) {   //если отчётов нет
                // берём все имена из названия отчётов
                List<String> reportTypeNames = animalReportTypeRepository.findAll()
                        .stream()
                        .map(AnimalReportType::getName)
                        .collect(Collectors.toList());
                String noReportMessage = reportTypeNames.stream()
                        .map(e -> String.format("За сегодня нет отчётов по: %s%n", e))
                        .collect(Collectors.joining());
                bot.execute(new SendMessage(animal.getUserId(), noReportMessage));
                log.debug(" No reports for today Message; {}", noReportMessage);
            } else {
                //  если отчёты есть
                Collection<AnimalReportData> reports =
                        reportDataRepository.findAllByState(AnimalReportStateEnum.CREATED);
                Map<Long, String> userReportMAp = reports.stream()
                        .collect(Collectors.toMap(AnimalReportData::getTelegramUser,
                                e -> String.format("Отсутствует отчёт: %s%n", e.getReportType().getName()),
                                (v, v1) -> v + v1));
                userReportMAp.forEach((k, v) -> bot.execute(new SendMessage(k, v)));
                log.debug("Message; {}", userReportMAp);
            }
        }
    }

    /**
     * При отсутствии отчётов в течении 2 дней бросает сообщение в чат поддержки о нарушении сдачи отчётов.
     */
    @Scheduled(cron = "0 0 21 */2 * *")
    public void remindForVolunteer() {
        String msgPattern = "Новый хозяин с id: %d не сдаёт отчёты по питомцу с id: %d и кличкой %s";
        animalRepository.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST)
                .forEach(animal -> animalReportRepository.findAllForTwoDays()
                        .stream()
                        .filter(animalReport -> animalReport.getUserId().equals(animal.getUserId()))
                        .findAny()
                        .orElseGet(() -> {
                            SendResponse response = bot.execute(new SendMessage(chatId,
                                    String.format(msgPattern, animal.getUserId(), animal.getId(), animal.getName())));
                            log.info("Message to support chat: {}", response);
                            return null;
                        }));
    }
}
