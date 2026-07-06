package com.jobshub.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyFormDto(

        @NotBlank(message = "Company name cannot be blank")
        @Size(min = 3, max = 50, message = "Company name must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Description cannot be blank")
        @Size(min = 10, max = 100, message = "Description must be between 10 and 500 characters")
        String description,

        String website,

        String headquarters
) {
}
