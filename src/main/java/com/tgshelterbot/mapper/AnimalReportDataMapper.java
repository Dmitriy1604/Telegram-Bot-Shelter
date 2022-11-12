package com.tgshelterbot.mapper;

import com.tgshelterbot.model.AnimalReportData;
import com.tgshelterbot.model.dto.AnimalReportDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalReportDataMapper {
    @Mapping(target = "localFileName", ignore = true)
    AnimalReportData toDto(AnimalReportData animalReportData);

    AnimalReportDataDto toEntity(AnimalReportData animalReportData);

    @Mapping(target = "localFileName", ignore = true)
    List<AnimalReportDataDto> toDtos(List<AnimalReportData> animalReportDataList);
}
