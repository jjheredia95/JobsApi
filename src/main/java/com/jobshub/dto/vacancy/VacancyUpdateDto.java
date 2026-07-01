package com.jobshub.dto.vacancy;

import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record VacancyUpdateDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 40, message = "Name must be between 3 and 40 characters")
        String name,//

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 100, message = "Description must be between 10 characters and 100 characters")
        String description,//

        @NotNull(message = "Category is required")
        @Positive(message = "Category id must be greater than 0")
        Integer categoryId,//

        @NotNull(message = "Open date is required")
        LocalDate openDate,//

        LocalDate closeDate,//

        @Positive(message = "Salary must be greater than 0")
        @DecimalMax(value = "1000000", inclusive = true, message = "Salary must not exceed 1000000")
        Double salary,//

        @Size(max = 255, message = "Image must not exceed 255 characters")
        @Pattern(
                regexp = "^(https?://\\S+|[a-zA-Z0-9._-]+)$",
                message = "Image must be a valid http(s) URL or file name"
        )
        String image,

        @Size(min = 10, max = 250, message = "Details must be between 10 and 250 characters")
        String details,

        @NotNull(message = "Company is required")
        @Positive(message = "Company id must be greater than 0")
        Integer companyId,

        @NotEmpty(message = "At least one location is required")
        List<@NotNull(message = "Location id cannot be blank")
        @Positive(message = "Location id must be greater than 0") Integer> locationIds,

        @NotNull(message = "Featured is required")
        Boolean featured,

        @NotNull(message = "Status is required")
        VacancyStatus status,

        @NotNull(message = "Work mode is required")
        WorkMode workMode,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType

) {
}
