package com.jobshub.dto.company;

public record CompanyResponseDto(
        Integer id,
        String name,
        String description,
        String website,
        String headquarters

) {
}
