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

import java.util.List;

@RestController
@RequestMapping("api/home")
public class HomeController {

    private final VacancyService vacancyService;

    public HomeController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public ResponseEntity<List<VacancyHomeDto>> homeVacancies() {
        System.out.println("homeVacancies: " + vacancyService.getHomeVacancies());
        return ResponseEntity.ok(vacancyService.getHomeVacancies());
    }










}
