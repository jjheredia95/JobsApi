package com.jobshub.controller;

import com.jobshub.dto.company.CompanyListDto;
import com.jobshub.junit.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping()
    public ResponseEntity<List<CompanyListDto>> getCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }







}
