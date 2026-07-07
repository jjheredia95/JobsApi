package com.jobshub.service;

import com.jobshub.dto.company.CompanyFormDto;
import com.jobshub.dto.company.CompanyResponseDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Company;
import com.jobshub.repository.CompanyRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    private Company buildExistingCompany() {
        Company existing = new Company();
        existing.setId(1);
        existing.setName("Uber");
        existing.setDescription("Transportation company for rideshared services");
        existing.setWebsite("https://uber.com");
        existing.setHeadquarters("San Francisco, CA");
        return existing;
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

    // ================= UPDATE ==================
    @Test
    void updateCompany_Success() {
        Company existing = buildExistingCompany();
        CompanyFormDto companyDto = buildNewCompanyDto();

        when(companyRepo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(companyRepo.existsByNameIgnoreCaseAndIdNot(companyDto.name(), existing.getId())).thenReturn(false);
        when(companyRepo.save(any(Company.class))).thenReturn(existing);

        CompanyResponseDto result = companyService.updateCompany(existing.getId(), companyDto);

        assertThat(result.name()).isEqualTo(companyDto.name());
        assertThat(result.description()).isEqualTo(companyDto.description());
        assertThat(result.website()).isEqualTo(companyDto.website());
        assertThat(result.headquarters()).isEqualTo(companyDto.headquarters());

        verify(companyRepo).save(existing);
    }

    @Test
    void updateCompany_ShouldThrow_WhenNameAlreadyExists() {
        Company existing = buildExistingCompany();
        CompanyFormDto companyDto = buildNewCompanyDto();

        when(companyRepo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(companyRepo.existsByNameIgnoreCaseAndIdNot(companyDto.name(), existing.getId())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> companyService.updateCompany(existing.getId(), companyDto));
        verify(companyRepo, never()).save(any());

    }

    // ================= Get a company by Id ==================
    @Test
    void getCompanyById_Success() {
        Company existing = buildExistingCompany();
        when(companyRepo.findById(existing.getId())).thenReturn(Optional.of(existing));

        CompanyResponseDto result = companyService.getCompanyById(existing.getId());

        assertThat(result.name()).isEqualTo(existing.getName());
        assertThat(result.description()).isEqualTo(existing.getDescription());
        assertThat(result.website()).isEqualTo(existing.getWebsite());
        assertThat(result.headquarters()).isEqualTo(existing.getHeadquarters());

    }

    @Test
    void getCompanyById_ShouldThrow_WhenIdNotFound() {
        Integer id = 1;
        when(companyRepo.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> companyService.getCompanyById(id));

    }








}
