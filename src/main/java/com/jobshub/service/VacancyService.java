package com.jobshub.service;

import com.jobshub.dto.location.LocationDto;
import com.jobshub.dto.vacancy.*;
import com.jobshub.dto.company.CompanyDetailsForVacancyDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Category;
import com.jobshub.model.Company;
import com.jobshub.model.Location;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.repository.CategoryRepo;
import com.jobshub.repository.CompanyRepo;
import com.jobshub.repository.LocationRepo;
import com.jobshub.repository.VacancyRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class VacancyService {

    private final VacancyRepo vacancyRepo;
    private final CategoryRepo categoryRepo;
    private final CompanyRepo companyRepo;
    private final LocationRepo locationRepo;
    private static final String ZONE_NAME = "America/New_York";
    private static final ZoneId APP_ZONE = ZoneId.of(ZONE_NAME);

    public VacancyService(VacancyRepo vacancyRepo,
                          CategoryRepo categoryRepo,
                          CompanyRepo companyRepo,
                          LocationRepo locationRepo) {
        this.vacancyRepo = vacancyRepo;
        this.categoryRepo = categoryRepo;
        this.companyRepo = companyRepo;
        this.locationRepo = locationRepo;
    }

    // FEATURED OPEN / PUBLISHED VACANCIES
    public Page<VacancyCardDto> getFeaturedVacancies(Pageable pageable) {
        Page<Vacancy> vacancies = vacancyRepo.findFeaturedOpenPublishedVacancies(pageable);
        return vacancies.map(vacancy -> new VacancyCardDto(
                vacancy.getId(),
                vacancy.getCategory().getName(),
                vacancy.getName(),
                vacancy.getCompany().getName(),
                vacancy.getLocations()
                        .stream()
                        .map(location -> new LocationDto(
                                location.getId(),
                                location.getCity(),
                                location.getState())).toList(),
                vacancy.getEmploymentType(),
                vacancy.getSalary(),
                vacancy.getDescription(),
                vacancy.getStatus(),
                vacancy.getPublishedDate(),
                vacancy.getOpenDate(),
                vacancy.getCloseDate()));
    }


    private LocalDate today() {
        return LocalDate.now(APP_ZONE);
    }

    public Page<VacancyAdminListDto> getAllVacanciesAdminList(String name, Integer categoryId, Integer companyId, String status, Pageable pageable) {

       VacancyStatus vacancyStatus = null;

       if (name != null && name.isBlank()) {
           name = null;
       }

        if (status != null && !status.isBlank()) {
            try {
                vacancyStatus = VacancyStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid vacancy status: " + status);

            }
        }

        Page<Vacancy> vacancies = vacancyRepo.searchAdminVacancies(name, categoryId, companyId, vacancyStatus, pageable);
        return vacancies.map(vacancy -> new VacancyAdminListDto(
                vacancy.getId(),
                vacancy.getCompany().getName(),
                vacancy.getCategory().getName(),
                vacancy.getName(),
                vacancy.getPublishedDate(),
                vacancy.getCloseDate(),
                vacancy.getStatus(),
                vacancy.getFeatured()));
    }


    public List<VacancyFullDto> getFullVacancyDetails() {
        List<Vacancy> vacancies = vacancyRepo.findAll();
        return vacancies.stream().map(this::toFullDto).toList();
    }



    // =================   CREATION OF A NEW VACANCY ========================
    public VacancyFullDto createNewVacancy(VacancyCreationDto vacancyDto, VacancyStatus targetStatus) {

        String vacancyName = vacancyDto.name();
        LocalDate openingDate = (targetStatus == VacancyStatus.OPEN) ? today() : vacancyDto.openDate();
        LocalDate closingDate = vacancyDto.closeDate();

        validateDates(openingDate, closingDate);
        Category category = findCategoryOrThrow(vacancyDto.categoryId());
        Company company = findCompanyOrThrow(vacancyDto.companyId());
        List<Location> locations = findLocationsOrThrow(vacancyDto.locationIds());
        validateUniqueVacancyForCompany(vacancyName, company.getId());

        Vacancy newVacancy = buildVacancy(vacancyDto, vacancyName, category, company, locations, targetStatus, openingDate);

        if (targetStatus != VacancyStatus.CREATED) {
            publish(newVacancy, targetStatus);
        }

        Vacancy savedVacancy = vacancyRepo.save(newVacancy);

        return toFullDto(savedVacancy);
    }

    // APPLY PUBLISHING: CHECK THE REQUIREMENTS AND MARK THE PUBLISHED DATE
    private void publish(Vacancy vacancy, VacancyStatus targetStatus) {
        validatePublishable(vacancy, targetStatus);
        vacancy.setPublishedDate(today());
    }

    // VALIDATE THAT A VACANCY HAS EVERYTHING REQUIRED TO BE PUBLISHED
    private void validatePublishable(Vacancy vacancy, VacancyStatus targetStatus) {

        if (vacancy.getLocations() == null || vacancy.getLocations().isEmpty()) {
            throw new BadRequestException("At least one location is required to publish a vacancy");
        }
        if (vacancy.getWorkMode() == null) {
            throw new BadRequestException("Work mode is required to publish a vacancy");
        }
        if (vacancy.getEmploymentType() == null) {
            throw new BadRequestException("Employment type is required to publish a vacancy");
        }

        if (targetStatus == VacancyStatus.PUBLISHED) {
            if (vacancy.getOpenDate() == null) {
                throw new BadRequestException("Open date is required to publish a vacancy");
            }
            if (!vacancy.getOpenDate().isAfter(today())) {
                throw new BadRequestException(
                        "Open date must be after today. To open it today, use 'Publish and open now'");
            }
        }

        if (vacancy.getCloseDate() != null && vacancy.getCloseDate().isBefore(today())) {
            throw new BadRequestException("Close date cannot be in the past");
        }
    }

    // VALIDATE DATE - CLOSE DATE CANNOT BE BEFORE OPENING DATE
    private void validateDates(LocalDate openDate, LocalDate closeDate) {
        if (closeDate != null && openDate != null) {
            if (closeDate.isBefore(openDate)) {
                throw new BadRequestException("Close date cannot be before open date");
            }
        }
    }

    // VALIDATE CATEGORY
    private Category findCategoryOrThrow(Integer categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
    }

    // VALIDATE COMPANY
    private Company findCompanyOrThrow(Integer companyId) {
        return companyRepo.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found with id: " + companyId));
    }

    // VALIDATE LOCATIONS
    private List<Location> findLocationsOrThrow(List<Integer> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            return List.of();
        }

        List<Integer> uniqueLocationIds = locationIds.stream().distinct().toList();
        List<Location> locations = locationRepo.findAllById(uniqueLocationIds);

        if (locations.size() != uniqueLocationIds.size()) {
            throw new NotFoundException("One or more locations were not found");
        }

        return locations;
    }

    // VALIDATE DUPLICATED VACANCY
    private void validateUniqueVacancyForCompany(String vacancyName, Integer companyId) {
        if (vacancyRepo.existsByNameIgnoreCaseAndCompany_Id(vacancyName, companyId)) {
            throw new BadRequestException("This company already has a vacancy with that name");
        }
    }

    // BUILD NEW VACANCY
    private Vacancy buildVacancy(
            VacancyCreationDto vacancyDto,
            String vacancyName,
            Category category,
            Company company,
            List<Location> locations,
            VacancyStatus targetStatus,
            LocalDate openingDate) {

        Vacancy newVacancy = new Vacancy();

        newVacancy.setName(vacancyName);
        newVacancy.setDescription(vacancyDto.description());
        newVacancy.setSalary(vacancyDto.salary());
        newVacancy.setOpenDate(openingDate);
        newVacancy.setCloseDate(vacancyDto.closeDate());
        newVacancy.setFeatured(false);
        newVacancy.setStatus(targetStatus);
        newVacancy.setCategory(category);
        newVacancy.setDetails(vacancyDto.details());
        newVacancy.setWorkMode(vacancyDto.workMode());
        newVacancy.setEmploymentType(vacancyDto.employmentType());
        newVacancy.setCompany(company);
        newVacancy.setLocations(locations);
        return newVacancy;
    }

    // ================= END OF VACANCY CREATION =====================

    // SHOW ALL DATA OF NEW CREATED VACANCY FROM BD
    private VacancyFullDto toFullDto(Vacancy vacancy) {
        return new VacancyFullDto(
                vacancy.getId(),
                vacancy.getName(),
                vacancy.getDescription(),
                vacancy.getSalary(),
                vacancy.getOpenDate(),
                vacancy.getCloseDate(),
                vacancy.getFeatured(),
                vacancy.getStatus(),
                vacancy.getCategory(),
                vacancy.getDetails(),
                vacancy.getPublishedDate(),
                vacancy.getWorkMode(),
                vacancy.getEmploymentType(),
                vacancy.getCompany(),
                vacancy.getCreatedAt(),
                vacancy.getLocations());
    }

    public Vacancy getVacancyById(Integer id) {
        return vacancyRepo.findById(id).orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));
    }

    public void deleteVacancy(Integer id) {
        Vacancy vacancy = getVacancyById(id);
        vacancyRepo.delete(vacancy);
    }

    public VacancyDetailsDto showVacancyDetails(Integer id) {
        Vacancy vacancy = getVacancyById(id);

        if (vacancy.getStatus() == VacancyStatus.CREATED) {
            throw new NotFoundException("Vacancy not found");
        }

        return new VacancyDetailsDto(
                vacancy.getCategory().getName(),
                vacancy.getName(),
                vacancy.getDescription(),
                vacancy.getPublishedDate(),
                vacancy.getOpenDate(),
                vacancy.getCloseDate(),
                vacancy.getStatus(),
                vacancy.getSalary(),
                vacancy.getDetails(),
                vacancy.getWorkMode(),
                vacancy.getEmploymentType(),
                new CompanyDetailsForVacancyDto(
                        vacancy.getCompany().getName(),
                        vacancy.getCompany().getDescription(),
                        vacancy.getCompany().getWebsite(),
                        vacancy.getCompany().getHeadquarters()
                ),
                vacancy.getLocations()
                .stream()
                .map(location -> new LocationDto(
                        location.getId(),
                        location.getCity(),
                        location.getState())).toList());
    }



    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = ZONE_NAME)
    public void closeExpiredVacancies() {
        LocalDate today = today();
        vacancyRepo.closeExpiredVacancies(VacancyStatus.OPEN, VacancyStatus.CLOSED, today);
    }



}
