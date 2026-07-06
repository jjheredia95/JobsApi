package com.jobshub.service;

import com.jobshub.dto.company.CompanyFormDto;
import com.jobshub.dto.company.CompanyResponseDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.model.Company;
import com.jobshub.repository.CompanyRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepo companyRepo;

    @InjectMocks
    private CompanyService companyService;

    private CompanyFormDto buildNewCompanyDto() {
        return new CompanyFormDto("Netflix",
                "Streaming entertainment service offering movies and shows",
                "https://netflix.com",
                "Los Gatos, CA");
    }

    @Test
    void createCompany_Success() {
        CompanyFormDto companyDto = buildNewCompanyDto();

        when(companyRepo.existsByNameIgnoreCase("Netflix")).thenReturn(false);

        Company newCompany = new Company();
        newCompany.setId(1);
        newCompany.setName(companyDto.name());
        newCompany.setDescription(companyDto.description());
        newCompany.setWebsite(companyDto.website());
        newCompany.setHeadquarters(companyDto.headquarters());

        when(companyRepo.save(any(Company.class))).thenReturn(newCompany);

        CompanyResponseDto result = companyService.createCompany(companyDto);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Netflix");
        assertThat(result.website()).isEqualTo("https://netflix.com");

    }

    @Test
    void createCompany_shouldThrowException_WhenNameAlreadyExists() {
        CompanyFormDto companyDto = buildNewCompanyDto();
        when(companyRepo.existsByNameIgnoreCase("Netflix")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> companyService.createCompany(companyDto));
        verify(companyRepo, never()).save(any());
    }















}
