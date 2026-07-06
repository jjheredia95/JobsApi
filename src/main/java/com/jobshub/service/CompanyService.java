package com.jobshub.service;

import com.jobshub.dto.company.CompanyFormDto;
import com.jobshub.dto.company.CompanyResponseDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Company;
import com.jobshub.repository.CompanyRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepo.findAll()
                .stream()
                .map(this::mappingToCompanyDto).toList();
    }

    public CompanyResponseDto createCompany(CompanyFormDto companyDto) {

        if (companyRepo.existsByNameIgnoreCase(companyDto.name())) {
            throw new BadRequestException("A company with this name already exists.");
        }

        Company company = new Company();
        company.setName(companyDto.name());
        company.setDescription(companyDto.description());
        company.setWebsite(companyDto.website());
        company.setHeadquarters(companyDto.headquarters());

        Company newCompany = companyRepo.save(company);
        return mappingToCompanyDto(newCompany);

    }

    // ============ getCompanyById ============
    public Company getCompanyById(Integer id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found with id: " + id));
    }

    // ============ updateCompany ============
    public CompanyResponseDto updateCompany(Integer id, CompanyFormDto dto) {
        Company existingCompany = getCompanyById(id);

        if (companyRepo.existsByNameIgnoreCaseAndIdNot(dto.name(), id)) {
            throw new BadRequestException("A company with this name already exists.");
        }

        existingCompany.setName(dto.name());
        existingCompany.setDescription(dto.description());
        existingCompany.setWebsite(dto.website());
        existingCompany.setHeadquarters(dto.headquarters());

        Company updated = companyRepo.save(existingCompany);
        return mappingToCompanyDto(updated);

    }

    // ============ helpers ============
    private CompanyResponseDto mappingToCompanyDto(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getWebsite(),
                company.getHeadquarters());
    }








}
