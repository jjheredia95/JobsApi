package com.jobshub.dto.vacancy;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record VacancyCreationDto(

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 40, message = "Name must be between 3 and 40 characters")
        String name,//

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 100, message = "Description must be between 10 characters and 100 characters")
        String description,//

        @NotNull(message = "Category is required")
        @Positive(message = "Category is required")
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
             @Positive(message = "Location id must be greater than 0") Integer> locationIds

) {
}
