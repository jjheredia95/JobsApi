package com.jobshub.junit;

import com.jobshub.dto.company.CompanyListDto;
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

    public List<CompanyListDto> getAllCompanies() {
        return companyRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private CompanyListDto mapToDto(Company company) {
        return new CompanyListDto(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getWebsite(),
                company.getHeadquarters()
        );
    }




}
