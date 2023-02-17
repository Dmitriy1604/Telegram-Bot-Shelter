package com.tgshelterbot.mapper;

import com.tgshelterbot.model.dto.SummarizedReport;
import com.tgshelterbot.model.dto.SummarizedReportDto;
import org.mapstruct.Mapper;
/**
 * Преобразователь из сущьностей в ДТО и обратно
 */
@Mapper(componentModel = "spring")
public interface SummarizedReportMapper {
    SummarizedReportDto toDto(SummarizedReport report);
}
