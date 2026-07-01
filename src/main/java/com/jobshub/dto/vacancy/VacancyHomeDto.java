package com.jobshub.dto.vacancy;

import com.jobshub.model.enums.VacancyStatus;

import java.time.LocalDate;

public record VacancyHomeDto(
        Integer id,
        String category,
        String name,
        LocalDate publishedDate,
        VacancyStatus status,
        String description

) {
}
