package com.tgshelterbot.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.crm.LocalizedMessages;
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
public class Reminder {
    private final AnimalReportDataRepository reportDataRepository;
    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportTypeRepository animalReportTypeRepository;

    private final AnimalRepository animalRepository;
    private final TelegramBot bot;
    private final LocalizedMessages localizedMessages;

    @Value("${telegram.bot.support.chat}")
    private String supportChatId;

    /**
     * С 8.00 утра до 20.00 вечера каждые 4 часа напоминает о необходимости сдать все или недостающие отчёты.
     */
    @Scheduled(cron = "0 0 8-20/4 * * *")//every 4 hours from 8 to 20
    public void remindAboutNoReports() {
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
                        .map(e -> String.format(localizedMessages.get("missed.report"), e))
                        .collect(Collectors.joining());
                bot.execute(new SendMessage(animal.getUserId(), noReportMessage));
                log.debug(" No reports for today Message; {}", noReportMessage);
            } else {
                //  если отчёты есть
                Collection<AnimalReportData> reports =
                        reportDataRepository.findAllByState(AnimalReportStateEnum.CREATED);
                Map<Long, String> userReportMAp = reports.stream()
                        .collect(Collectors.toMap(AnimalReportData::getTelegramUser,
                                e -> String.format(localizedMessages.get("lack.report"), e.getReportType().getName()),
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
    public void SendAttentionToVolunteer() {
        String msgPattern = localizedMessages.get("attention.msg");
        animalRepository.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST)
                .forEach(animal -> animalReportRepository.findAllForTwoDays()
                        .stream()
                        .filter(animalReport -> animalReport.getUserId().equals(animal.getUserId()))
                        .findAny()
                        .orElseGet(() -> {
                            SendResponse response = bot.execute(new SendMessage(supportChatId,
                                    String.format(msgPattern, animal.getUserId(), animal.getId(), animal.getName())));
                            log.debug("Message to support chat: {}", response);
                            return null;
                        }));
    }

    /**
     * Перед началом просмотра отчетов волантерами проводится проверка испытвтельного срока по животным
     * в случае окончания срока отправляется уведомление о необходимрсти принять решение
     */
    @Scheduled(cron = "0 59 20 * * *")
    public void RemindOfEndTestPeriod() {
        String msgPattern = localizedMessages.get("decision.msg");
        animalRepository.findAllWithEndPeriod().forEach(animal -> {
            SendResponse response = bot.execute(new SendMessage(supportChatId,
                    String.format(msgPattern, animal.getUserId(), animal.getName())));
            log.debug("End period message: {}", response.message().text());
        });
    }
}
