package com.jobshub.service;

import com.jobshub.dto.vacancy.VacancyCreationDto;
import com.jobshub.dto.vacancy.VacancyFullDto;
import com.jobshub.dto.vacancy.VacancyUpdateDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Category;
import com.jobshub.model.Company;
import com.jobshub.model.Location;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;
import com.jobshub.repository.CategoryRepo;
import com.jobshub.repository.CompanyRepo;
import com.jobshub.repository.LocationRepo;
import com.jobshub.repository.VacancyRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock private VacancyRepo vacancyRepo;
    @Mock private CategoryRepo categoryRepo;
    @Mock private CompanyRepo companyRepo;
    @Mock private LocationRepo locationRepo;

    @InjectMocks
    private VacancyService vacancyService;

    private Category category;
    private Company company;
    private List<Location> locations;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Technology");

        company = new Company();
        company.setId(1);
        company.setName("TechCorp");

        Location location = new Location();
        location.setId(1);
        location.setCity("New York");
        location.setState("NY");

        locations = List.of(location);

    }

    private VacancyCreationDto buildValidDto() {
        return new VacancyCreationDto(
                "Backend Developer",
                "Build and maintain backend services",
                1, LocalDate.now(),
                LocalDate.now().plusDays(30),
                50000.0, "image.png", "Some extra details about the role", 1,
                List.of(1));
    }

    private VacancyUpdateDto buildValidUpdateDto() {
        return new VacancyUpdateDto("Backend Developer",
                "Build and maintain backend services",
                1,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                65000.0, "image.jpg",
                "Some extra details about the role",
                1, List.of(1),
                false,
                VacancyStatus.OPEN,
                WorkMode.ONSITE,
                EmploymentType.FULL_TIME);
    }

    private Vacancy buildExistingVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1);
        vacancy.setName("Old Name");
        vacancy.setPublishedDate(LocalDate.now().minusDays(5));
        return vacancy;
    }


    @Test
    void createNewVacancy_success() {
        VacancyCreationDto dto = buildValidDto();

        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(locations);
        when(vacancyRepo.existsByNameIgnoreCaseAndCompany_Id("Backend Developer", 1)).thenReturn(false);
        when(vacancyRepo.save(any(Vacancy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VacancyFullDto result = vacancyService.createNewVacancy(dto);

        assertThat(result.name()).isEqualTo("Backend Developer");
        verify(vacancyRepo).save(any(Vacancy.class));
    }

    @Test
    void createNewVacancy_ShouldThrow_WhenOpenDateBeforePublishedDate() {
        VacancyCreationDto vacancy = new VacancyCreationDto(
                "Backend Developer",
                "Build and maintain backend services",
                1, LocalDate.now().minusDays(1), null,
                50000.0, "image.png", "Some extra details about the role", 1,
                List.of(1));

        assertThrows(BadRequestException.class, () -> vacancyService.createNewVacancy(vacancy));
        verify(vacancyRepo, never()).save(any());
    }



    @Test
    void createNewVacancy_ShouldThrow_WhenCloseDateBeforeOpenDate() {
        VacancyCreationDto dto = new VacancyCreationDto(
                "Backend Developer", "Build and maintain backend services", 1,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(5), // closeDate antes de openDate
                50000.0, "image.png", "Some extra details about the role",
                1, List.of(1)
        );

        assertThrows(BadRequestException.class, () -> vacancyService.createNewVacancy(dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void createNewVacancy_ShouldThrow_WhenCategoryNotFound() {
        VacancyCreationDto dto = buildValidDto();
        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vacancyService.createNewVacancy(dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void createNewVacancy_ShouldThrow_WhenCompanyNotFound() {
        VacancyCreationDto dto = buildValidDto();
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vacancyService.createNewVacancy(dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void createNewVacancy_ShouldThrow_WhenLocationsNotFound() {
        VacancyCreationDto dto = buildValidDto();
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(List.of()); // ninguna location encontrada

        assertThrows(NotFoundException.class, () -> vacancyService.createNewVacancy(dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void createNewVacancy_ShouldThrow_WhenVacancyNameAlreadyExistsForCompany() {
        VacancyCreationDto dto = buildValidDto();
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(locations);
        when(vacancyRepo.existsByNameIgnoreCaseAndCompany_Id("Backend Developer", 1)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vacancyService.createNewVacancy(dto));
        verify(vacancyRepo, never()).save(any());
    }

    // ==================== Update Vacancy ========================

    @Test
    void updateVacancy_Success() {
        Vacancy existing = buildExistingVacancy();
        VacancyUpdateDto dto = buildValidUpdateDto();

        when(vacancyRepo.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(locations);
        when(vacancyRepo.existsByNameIgnoreCaseAndCompany_IdAndIdNot("Backend Developer", 1, 1)).thenReturn(false);
        when(vacancyRepo.save(any(Vacancy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> vacancyService.updateVacancy(1, dto));
        verify(vacancyRepo).save(any(Vacancy.class));
    }

    @Test
    void updateVacancy_ShouldThrow_WhenVacancyNotFound() {
        when(vacancyRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vacancyService.updateVacancy(99, buildValidUpdateDto()));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenOpenDateBeforePublishedDate() {
        Vacancy existing = buildExistingVacancy(); // publishedDate = hace 5 días
        VacancyUpdateDto dto = new VacancyUpdateDto(
                "Backend Developer", "Build and maintain backend services", 1,
                LocalDate.now().minusDays(10), // openDate antes de publishedDate
                null, 50000.0, "image.png", "Some extra details about the role",
                1, List.of(1), false, VacancyStatus.OPEN, WorkMode.ONSITE, EmploymentType.FULL_TIME
        );

        when(vacancyRepo.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> vacancyService.updateVacancy(1, dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenCloseDateBeforeOpenDate() {
        Vacancy existing = buildExistingVacancy();
        VacancyUpdateDto dto = new VacancyUpdateDto(
                "Backend Developer", "Build and maintain backend services", 1,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(5), // closeDate antes de openDate
                50000.0, "image.png", "Some extra details about the role",
                1, List.of(1), false, VacancyStatus.OPEN, WorkMode.ONSITE, EmploymentType.FULL_TIME
        );

        when(vacancyRepo.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> vacancyService.updateVacancy(1, dto));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenCategoryNotFound() {
        when(vacancyRepo.findById(1)).thenReturn(Optional.of(buildExistingVacancy()));
        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vacancyService.updateVacancy(1, buildValidUpdateDto()));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenCompanyNotFound() {
        when(vacancyRepo.findById(1)).thenReturn(Optional.of(buildExistingVacancy()));
        when(categoryRepo.findById(1)).thenReturn(Optional.of(new Category()));
        when(companyRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vacancyService.updateVacancy(1, buildValidUpdateDto()));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenLocationsNotFound() {
        when(vacancyRepo.findById(1)).thenReturn(Optional.of(buildExistingVacancy()));
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> vacancyService.updateVacancy(1, buildValidUpdateDto()));
        verify(vacancyRepo, never()).save(any());
    }

    @Test
    void updateVacancy_ShouldThrow_WhenNameAlreadyExistsForSameCompany() {
        when(vacancyRepo.findById(1)).thenReturn(Optional.of(buildExistingVacancy()));
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(companyRepo.findById(1)).thenReturn(Optional.of(company));
        when(locationRepo.findAllById(List.of(1))).thenReturn(locations);
        when(vacancyRepo.existsByNameIgnoreCaseAndCompany_IdAndIdNot("Backend Developer", 1, 1)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vacancyService.updateVacancy(1, buildValidUpdateDto()));
        verify(vacancyRepo, never()).save(any());
    }










}
