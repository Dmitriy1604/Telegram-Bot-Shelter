package com.tgshelterbot.mapper;

import com.tgshelterbot.model.AnimalReport;
import com.tgshelterbot.model.dto.AnimalReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = AnimalReportDataMapper.class)
public interface AnimalReportMapper {
    @Mapping(source = "animalReportDataList", target = "animalReportDataDtos")
    AnimalReportDto toDto(AnimalReport animalReport);

    @Mapping(source = "animalReportDataList", target = "animalReportDataDtos")
    List<AnimalReportDto> toDtos(List<AnimalReport> animalReportList);

    @Mapping(source = "animalReportDataDtos", target = "animalReportDataList")
    @Mapping(source = "message", target = "message")
    AnimalReport toEntity(AnimalReportDto animalReportDto);

    @Mapping(source = "animalReportDataDtos", target = "animalReportDataList")
    @Mapping(source = "message", target = "message")
    List<AnimalReport> toEntities(List<AnimalReportDto> animalReportDto);

}