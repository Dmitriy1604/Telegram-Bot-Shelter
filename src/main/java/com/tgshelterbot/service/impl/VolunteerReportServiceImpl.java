package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.AnimalReportDataMapper;
import com.tgshelterbot.mapper.AnimalReportMapper;
import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.model.MessageForSend;
import com.tgshelterbot.model.dto.AnimalReportDto;
import com.tgshelterbot.repository.AnimalReportDataRepository;
import com.tgshelterbot.repository.AnimalReportRepository;
import com.tgshelterbot.repository.MessageForSendRepository;
import com.tgshelterbot.service.VolunteerReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class VolunteerReportServiceImpl implements VolunteerReportService {
    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportDataRepository animalReportDataRepository;
    private final AnimalReportMapper animalReportMapper;
    private final AnimalReportDataMapper animalReportDataMapper;

    private final MessageForSendRepository messageForSendRepository;

    @Override
    public List<AnimalReportDto> getAllReportsInPeriod(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd) {
        List<AnimalReport> all = animalReportRepository.findAllByDtCreateBetweenOrderById(PageRequest.of(page - 1, size), dtStart, dtEnd);

        return animalReportMapper.toDtos(all);
    }

    @Override
    public List<AnimalReportDto> getAllReportsInPeriodFilterState(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd, AnimalReportStateEnum stateEnum) {
        List<AnimalReport> all = animalReportRepository.findAllByDtCreateBetweenAndStateOrderById(PageRequest.of(page - 1, size), dtStart, dtEnd, stateEnum);

        return animalReportMapper.toDtos(all);
    }

    @Override
    public AnimalReportDto getReportForView(Long reportId) {
        AnimalReport animalReport = animalReportRepository.findById(reportId).orElseThrow(EntityNotFoundException::new);
        return animalReportMapper.toDto(animalReport);
    }

    @Override
    public AnimalReportDto viewReport(Long reportId, AnimalReportDto animalReportDto) {
        AnimalReport animalReport = animalReportRepository.findById(reportId).orElseThrow(EntityNotFoundException::new);
        AnimalReport animalReportNew = animalReportMapper.toEntity(animalReportDto);

        //отчет на доработку
        if (animalReportNew.getState().equals(AnimalReportStateEnum.REJECTED) || animalReportNew.getState().equals(AnimalReportStateEnum.CREATED)) {
            //CREATED - так как REJECTED ставим по таймауту, если не досдал вовремя
            animalReport.setState(AnimalReportStateEnum.CREATED);
        }
        if (animalReportNew.getState().equals(AnimalReportStateEnum.ACCEPT)) {
            animalReport.setState(animalReportNew.getState());
            animalReport.setDtAccept(OffsetDateTime.now());
        }

        List<AnimalReportData> animalReportDataList = animalReport.getAnimalReportDataList();
        List<AnimalReportData> animalReportDataListNew = animalReportNew.getAnimalReportDataList();
        for (AnimalReportData reportData : animalReportDataList) {
            log.info(reportData.toString());
            AnimalReportData current = animalReportDataListNew.stream().filter(animalReportData -> animalReportData.getId().equals(reportData.getId())).findFirst().orElseThrow(EntityNotFoundException::new);

            if (current.getState().equals(AnimalReportStateEnum.ACCEPT)) {
                reportData.setState(AnimalReportStateEnum.ACCEPT);
                reportData.setDtAccept(OffsetDateTime.now());
            }
            if (current.getState().equals(AnimalReportStateEnum.REJECTED) || current.getState().equals(AnimalReportStateEnum.CREATED)) {
                reportData.setState(AnimalReportStateEnum.CREATED);
                reportData.setDtAccept(OffsetDateTime.now());
            }
        }
        animalReport.setAnimalReportDataList(animalReportDataList);
        animalReport.setMessage(animalReportNew.getMessage());


        // Отправка пользователю коммента от волонтера
        if (animalReportNew.getMessage() != null && animalReportNew.getMessage().length() >= 1) {
            MessageForSend messageForSend = new MessageForSend();
            messageForSend.setDtNeedSend(OffsetDateTime.now());
            messageForSend.setText("Отчет: " + animalReportNew.getDtCreate().truncatedTo(ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())) + ", сообщение от волонтера: \n" + animalReportNew.getMessage());
            messageForSend.setUserId(animalReportNew.getUserId());
            messageForSendRepository.save(messageForSend);
        }


        return animalReportMapper.toDto(animalReportRepository.save(animalReport));
    }
}
