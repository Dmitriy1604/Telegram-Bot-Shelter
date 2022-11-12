package com.tgshelterbot.mapper;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.dto.AnimalReportDataDto;
import com.tgshelterbot.model.dto.AnimalReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = AnimalReportDataMapper.class)
public interface AnimalReportMapper {


    AnimalReportDto toDto(AnimalReport animalReport);

    @Mapping(source = "animalReport.id", target = "id")
    @Mapping(source = "animalReport.animal", target = "animal")
    @Mapping(source = "animalReport.dtCreate", target = "dtCreate")
    @Mapping(source = "animalReport.dtAccept", target = "dtAccept")
    @Mapping(source = "animalReport.state", target = "state")
    AnimalReportDto toDto(AnimalReport animalReport, List<AnimalReportDataDto> animalReportDataDtos);

}