package com.jobshub.controller;

import com.jobshub.dto.vacancy.VacancyHomeDto;
import com.jobshub.model.enums.EmploymentType;
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
    public ResponseEntity<Page<VacancyHomeDto>> getHomeVacancies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) Integer locationId,
            @RequestParam(required = false) String employmentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<VacancyHomeDto> result = vacancyService.homeVacancies(search, categoryId, workMode, locationId, employmentType, pageable);

        return ResponseEntity.ok(result);
    }










}
