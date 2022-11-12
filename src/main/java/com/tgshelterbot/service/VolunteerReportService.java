package com.tgshelterbot.service;

import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.model.dto.AnimalReportDto;

import java.time.OffsetDateTime;
import java.util.List;

public interface VolunteerReportService {
    List<AnimalReportDto> getAllReportsInPeriod(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd);

    List<AnimalReportDto> getAllReportsInPeriodFilterState(int page, int size, OffsetDateTime dtStart, OffsetDateTime dtEnd, AnimalReportStateEnum stateEnum);


    void getReportForView(Long reportId);
}
