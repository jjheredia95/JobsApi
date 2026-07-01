package com.jobshub.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationCreationDto(
        @NotBlank(message = "City is required")
        @Size(max = 20, message = "City must not exceed 20 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 20, message = "State must not exceed 20 characters")
        String state
) {
}
