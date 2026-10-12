package com.jobshub.dto.vacancy;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record VacancyCreationDto(

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 40, message = "Name must be between 3 and 40 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 100, message = "Description must be between 10 characters and 100 characters")
        String description,

        @NotNull(message = "Category is required")
        @Positive(message = "Category id must be positive")
        Integer categoryId,

        LocalDate openDate,
        LocalDate closeDate,
        WorkMode workMode,
        EmploymentType employmentType,

        @NotNull(message = "Featured selection is required")
        Boolean featured,

        @NotNull(message = "Status selection is required")
        VacancyStatus status,

        @Positive(message = "Salary must be greater than 0")
        @DecimalMax(value = "1000000", inclusive = true, message = "Salary must not exceed 1000000")
        Double salary,


        @Size(min = 10, max = 250, message = "Details must be between 10 and 250 characters")
        String details,//;-)

        @NotNull(message = "Company is required")
        @Positive(message = "Company id must be greater than 0")
        Integer companyId, //;-)

        @NotEmpty(message = "At least one location is required")
        List<@NotNull(message = "Location id cannot be blank")
             @Positive(message = "Location id must be greater than 0")
                Integer> locationIds//;-)

) {
}
