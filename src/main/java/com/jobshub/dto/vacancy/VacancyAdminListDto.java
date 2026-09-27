package com.jobshub.dto.vacancy;

import com.jobshub.model.enums.VacancyStatus;

import java.time.LocalDate;

public record VacancyAdminListDto(
        Integer id,
        String companyName,
        String categoryName,
        String name,
        LocalDate publishedDate,
        LocalDate closingDate,
        VacancyStatus status,
        Boolean featured

) {
}


