package com.jobshub.dto.vacancy;

import com.jobshub.model.Category;
import com.jobshub.model.Company;
import com.jobshub.model.Location;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record VacancyFullDto(
        Integer id,
        String name,
        String description,
        Double salary,
        LocalDate openDate,
        LocalDate closeDate,
        Boolean featured,
        VacancyStatus status,
        Category category,
        String details,
        LocalDate publishedDate,
        WorkMode workMode,
        EmploymentType employmentType,
        Company company,
        Instant createdAt,
        List<Location> locations


)
{

}