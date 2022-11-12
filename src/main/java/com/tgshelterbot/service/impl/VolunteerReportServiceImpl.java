package com.tgshelterbot.service.impl;

import com.tgshelterbot.mapper.AnimalReportDataMapper;
import com.tgshelterbot.mapper.AnimalReportMapper;
import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.model.dto.AnimalReportDto;
import com.tgshelterbot.repository.AnimalReportDataRepository;
import com.tgshelterbot.repository.AnimalReportRepository;
import com.tgshelterbot.service.VolunteerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerReportServiceImpl implements VolunteerReportService {
    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportDataRepository animalReportDataRepository;
    private final AnimalReportMapper animalReportMapper;
    private final AnimalReportDataMapper animalReportDataMapper;

    @Override
    public List<AnimalReportDto> getAllReportsInPeriod(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd) {
        List<AnimalReport> all = animalReportRepository.findAllByDtCreateBetweenOrderById(PageRequest.of(page - 1, size), dtStart, dtEnd);

        return all.stream()
                .map(animalReport -> animalReportMapper.toDto(animalReport,
                                animalReportDataMapper.toDtos(animalReportDataRepository.findAllByAnimalReport(animalReport))
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<AnimalReportDto> getAllReportsInPeriodFilterState(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd, AnimalReportStateEnum stateEnum) {
        List<AnimalReport> all = animalReportRepository.findAllByDtCreateBetweenAndStateOrderById(PageRequest.of(page - 1, size), dtStart, dtEnd, stateEnum);

        return all.stream()
                .map(animalReport -> animalReportMapper.toDto(animalReport,
                                animalReportDataMapper.toDtos(animalReportDataRepository.findAllByAnimalReport(animalReport))
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public void getReportForView(Long reportId) {
        // Выдать главный отчет и в виде вложенного json все вложенные + сообщение от волонтера
        // если сообщение не null тогда кидаем в планировщик задание на отправку и ветвление принят или отклонен отчет
        // Волонтер отклонил отчет {Date/Type}: комментарий / Волонтер принял отчет {Date/Type} : комментарий

    }
}
