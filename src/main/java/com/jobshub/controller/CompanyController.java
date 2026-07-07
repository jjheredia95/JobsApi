package com.jobshub.controller;

import com.jobshub.dto.company.CompanyFormDto;
import com.jobshub.dto.company.CompanyResponseDto;
import com.jobshub.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping()
    public ResponseEntity<List<CompanyResponseDto>> getCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PostMapping("/create")
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyFormDto request) {
        CompanyResponseDto created = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Integer id, @Valid @RequestBody CompanyFormDto request) {
        CompanyResponseDto updated = companyService.updateCompany(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable Integer id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }









}
