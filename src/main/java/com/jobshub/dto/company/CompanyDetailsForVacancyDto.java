package com.jobshub.dto.company;

public record CompanyDetailsForVacancyDto(
        String name,
        String description,
        String website,
        String headquarters
) {
}
