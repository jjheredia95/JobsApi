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
    public ResponseEntity<Page<VacancyHomeDto>> homeVacancies(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer idCategory,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VacancyHomeDto> result = vacancyService.getHomeVacancies(description, idCategory, pageable);
        return ResponseEntity.ok(result);
    }












}
