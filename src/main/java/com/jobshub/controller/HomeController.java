package com.jobshub.controller;

import com.jobshub.dto.vacancy.VacancyHomeDto;
import com.jobshub.service.VacancyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/home")
public class HomeController {

    private final VacancyService vacancyService;

    public HomeController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public ResponseEntity<?> getHomeVacancies(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) Integer locationId,
            @RequestParam() int page,
            @RequestParam() int size) {

        System.out.println("Description: " + description);
        System.out.println("CategoryId: " + categoryId);
        System.out.println("Work Mode: " + workMode);
        System.out.println("LocationId: " + locationId);

        Pageable pageable = PageRequest.of(page, size);
        Page<VacancyHomeDto> result = vacancyService.homeVacancies(description, categoryId, workMode, locationId, pageable);

        if (result.isEmpty()) {
            return ResponseEntity.ok("No available vacancies");
        }
        return ResponseEntity.ok(result);
    }










}
