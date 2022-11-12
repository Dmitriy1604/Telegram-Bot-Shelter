package com.tgshelterbot.controller;

import com.tgshelterbot.model.AnimalReportStateEnum;
import com.tgshelterbot.model.dto.AnimalReportDto;
import com.tgshelterbot.service.VolunteerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/volunteer/report")
@RequiredArgsConstructor
public class VolunteerReportController {
    private final VolunteerReportService volunteerReportService;

    @GetMapping(params = {"page", "limit", "dt_start", "dt_end"})
    public List<AnimalReportDto> getAllReportsInPeriod(@RequestParam("page") int page,
                                                       @RequestParam("limit") int limit,
                                                       @RequestParam("dt_start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dtStart,
                                                       @RequestParam("dt_end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dtEnd) {
        return volunteerReportService.getAllReportsInPeriod(page, limit, dtStart, dtEnd);
    }

    @GetMapping(params = {"page", "limit", "dt_start", "dt_end", "state"})
    public List<AnimalReportDto> getAllReportsInPeriodFilterState(@RequestParam("page") int page,
                                                                  @RequestParam("limit") int limit,
                                                                  @RequestParam("dt_start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dtStart,
                                                                  @RequestParam("dt_end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dtEnd,
                                                                  @RequestParam(value = "state", required = false) AnimalReportStateEnum stateEnum
    ) {
        return volunteerReportService.getAllReportsInPeriodFilterState(page, limit, dtStart, dtEnd, stateEnum);
    }


}
