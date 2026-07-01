package com.jobshub.dto.vacancy;

import com.jobshub.model.enums.VacancyStatus;

import java.time.LocalDate;

public record VacancyListDto(
        Integer id,
        String category,
        String name,
        LocalDate publishedDate,
        Boolean featured,
        VacancyStatus status
) {
}


