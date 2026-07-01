package com.jobshub.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryFormResponseDto(
        @NotBlank(message = "Category name cannot be blank")
        String name,

        @NotBlank(message = "Description cannot be blank")
        String description
) {
}
