package com.tgshelterbot.mapper;

import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.dto.AnimalReportDataDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalReportDataMapper {
    AnimalReportData toDto(AnimalReportData animalReportData);

    AnimalReportDataDto toEntity(AnimalReportData animalReportData);

    List<AnimalReportDataDto> toDtos(List<AnimalReportData> animalReportDataList);

    List<AnimalReportData> toEntities(List<AnimalReportDataDto> animalReportDataList);
}
