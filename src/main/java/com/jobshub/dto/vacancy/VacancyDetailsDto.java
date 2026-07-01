package com.jobshub.dto.vacancy;
import com.jobshub.dto.company.CompanyDetailsForVacancyDto;
import com.jobshub.dto.location.LocationDto;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;

import java.time.LocalDate;
import java.util.List;

public record VacancyDetailsDto(
        String category,
        String name,
        String description,
        LocalDate publishedDate,
        LocalDate openDate,
        LocalDate closeDate,
        VacancyStatus status,
        Double salary,
        String details,
        WorkMode workMode,
        EmploymentType employmentType,
        CompanyDetailsForVacancyDto company,
        List<LocationDto> locations


) {
}
