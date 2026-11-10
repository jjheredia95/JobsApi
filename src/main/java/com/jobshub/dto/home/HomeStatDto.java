package com.jobshub.dto.home;

public record HomeStatDto (
        long jobsOpenCount,
        long companiesCount,
        long categoriesCount,
        long citiesCount
) {
}
