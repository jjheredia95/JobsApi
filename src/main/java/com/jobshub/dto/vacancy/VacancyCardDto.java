package com.jobshub.dto.vacancy;

import com.jobshub.dto.location.LocationDto;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;

import java.time.LocalDate;
import java.util.List;

public record VacancyCardDto(
        Integer id,
        String category,
        String name,
        String companyName,
        List<LocationDto> locations,
        EmploymentType employmentType,
        Double salary,
        String description,
        VacancyStatus status,
        LocalDate publishedDate,
        LocalDate openDate,
        LocalDate closeDate

) {
}
