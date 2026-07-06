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
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;
import com.jobshub.repository.CategoryRepo;
import com.jobshub.repository.CompanyRepo;
import com.jobshub.repository.LocationRepo;
import com.jobshub.repository.VacancyRepo;
import com.jobshub.specification.VacancySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VacancyService {

    private final VacancyRepo vacancyRepo;
    private final CategoryRepo categoryRepo;
    private final CompanyRepo companyRepo;
    private final LocationRepo locationRepo;

    public VacancyService(VacancyRepo vacancyRepo,
                          CategoryRepo categoryRepo,
                          CompanyRepo companyRepo,
                          LocationRepo locationRepo) {
        this.vacancyRepo = vacancyRepo;
        this.categoryRepo = categoryRepo;
        this.companyRepo = companyRepo;
        this.locationRepo = locationRepo;
    }

    public Page<VacancyListDto> getAllVacancies(Pageable pageable) {
        Page<Vacancy> vacancies = vacancyRepo.findAll(pageable);
        return vacancies.map(this::toListDto);
    }

    public List<VacancyFullDto> getFullVacancyDetails() {
        List<Vacancy> vacancies = vacancyRepo.findAll();
        return vacancies.stream().map(this::toFullDto).toList();
    }

    //Create a vacancy
    public VacancyFullDto createNewVacancy(VacancyCreationDto vacancyDto) {
        String vacancyName = vacancyDto.name().trim();
        LocalDate publishedDate = LocalDate.now();

        validateDates(vacancyDto, publishedDate);

        Category category = findCategoryOrThrow(vacancyDto.categoryId());
        Company company = findCompanyOrThrow(vacancyDto.companyId());
        List<Location> locations = findLocationsOrThrow(vacancyDto.locationIds());

        validateUniqueVacancyForCompany(vacancyName, company.getId());

        Vacancy newVacancy = buildVacancy(vacancyDto, vacancyName, publishedDate, category, company, locations);

        Vacancy savedVacancy = vacancyRepo.save(newVacancy);

        return toFullDto(savedVacancy);
    }

    public VacancyFullDto updateVacancy(Integer id, VacancyUpdateDto vacancyDto) {
        Vacancy vacancy = findVacancyOrThrow(id);
        String vacancyName = vacancyDto.name().trim();

        validateDates(vacancyDto, vacancy.getPublishedDate());

        Category category = findCategoryOrThrow(vacancyDto.categoryId());
        Company company = findCompanyOrThrow(vacancyDto.companyId());
        List<Location> locations = findLocationsOrThrow(vacancyDto.locationIds());

        validateUniqueVacancyForUpdate(vacancyName, company.getId(), vacancy.getId());

        updateVacancyFields(vacancy, vacancyDto, vacancyName, category, company, locations);

        Vacancy savedVacancy = vacancyRepo.save(vacancy);
        return toFullDto(savedVacancy);
    }

    private VacancyListDto toListDto(Vacancy vacancy) {
        return new VacancyListDto(
                vacancy.getId(),
                vacancy.getCategory().getName(),
                vacancy.getName(),
                vacancy.getPublishedDate(),
                vacancy.getFeatured(),
                vacancy.getStatus()
        );
    }

    private VacancyFullDto toFullDto(Vacancy vacancy) {
        return new VacancyFullDto(
                vacancy.getId(),
                vacancy.getName(),
                vacancy.getDescription(),
                vacancy.getSalary(),
                vacancy.getPublishedDate(),
                vacancy.getOpenDate(),
                vacancy.getCloseDate(),
                vacancy.getFeatured(),
                vacancy.getStatus(),
                vacancy.getCategory(),
                vacancy.getDetails(),
                vacancy.getImage(),
                vacancy.getWorkMode(),
                vacancy.getEmploymentType(),
                vacancy.getCompany(),
                vacancy.getLocations()
        );
    }

    // overload method to create and update Dto
    private void validateDates(VacancyCreationDto vacancyDto, LocalDate publishedDate) {
        validateCorrectDates(vacancyDto.openDate(), vacancyDto.closeDate(), publishedDate);
    }

    private void validateDates(VacancyUpdateDto vacancyDto, LocalDate publishedDate) {
        validateCorrectDates(vacancyDto.openDate(), vacancyDto.closeDate(), publishedDate);
    }

    // Reusing logic
    private void validateCorrectDates(LocalDate openDate, LocalDate closeDate, LocalDate publishedDate) {
        if (openDate.isBefore(publishedDate)) {
            throw new BadRequestException("Open date cannot be before published date");
        }

        if (closeDate != null && closeDate.isBefore(openDate)) {
            throw new BadRequestException("Close date cannot be before open date");
        }
    }


    private Category findCategoryOrThrow(Integer categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
    }

    private Company findCompanyOrThrow(Integer companyId) {
        return companyRepo.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found with id: " + companyId));
    }

    private Vacancy findVacancyOrThrow(Integer id) {
        return vacancyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));
    }

    private List<Location> findLocationsOrThrow(List<Integer> locationIds) {
        List<Integer> uniqueLocationIds = locationIds.stream().distinct().toList();
        List<Location> locations = locationRepo.findAllById(uniqueLocationIds);

        if (locations.size() != uniqueLocationIds.size()) {
            throw new NotFoundException("One or more locations were not found");
        }

        return locations;
    }

    private void validateUniqueVacancyForCompany(String vacancyName, Integer companyId) {
        if (vacancyRepo.existsByNameIgnoreCaseAndCompany_Id(vacancyName, companyId)) {
            throw new BadRequestException("This company already has a vacancy with that name");
        }
    }

    private void validateUniqueVacancyForUpdate(String vacancyName, Integer companyId, Integer vacancyId) {
        if (vacancyRepo.existsByNameIgnoreCaseAndCompany_IdAndIdNot(vacancyName, companyId, vacancyId)) {
            throw new BadRequestException("This company already has a vacancy with that name");
        }
    }

    private String resolveImageName(VacancyUpdateDto vacancyDto, String currentImage) {
        return vacancyDto.image() != null ? vacancyDto.image() : currentImage;
    }

    private void updateVacancyFields(Vacancy vacancy, VacancyUpdateDto vacancyDto, String vacancyName,
                                     Category category, Company company, List<Location> locations) {
        vacancy.setName(vacancyName);
        vacancy.setDescription(vacancyDto.description());
        vacancy.setSalary(vacancyDto.salary());
        vacancy.setOpenDate(vacancyDto.openDate());
        vacancy.setCloseDate(vacancyDto.closeDate());
        vacancy.setFeatured(vacancyDto.featured());
        vacancy.setStatus(vacancyDto.status());
        vacancy.setCategory(category);
        vacancy.setDetails(vacancyDto.details());
        vacancy.setImage(resolveImageName(vacancyDto, vacancy.getImage()));
        vacancy.setWorkMode(vacancyDto.workMode());
        vacancy.setEmploymentType(vacancyDto.employmentType());
        vacancy.setCompany(company);
        vacancy.setLocations(locations);
    }


    private Vacancy buildVacancy(VacancyCreationDto vacancyDto, String vacancyName, LocalDate publishedDate,
                                 Category category, Company company, List<Location> locations) {

        Vacancy newVacancy = new Vacancy();
        newVacancy.setName(vacancyName);
        newVacancy.setDescription(vacancyDto.description());
        newVacancy.setSalary(vacancyDto.salary());
        newVacancy.setPublishedDate(publishedDate);
        newVacancy.setOpenDate(vacancyDto.openDate());
        newVacancy.setCloseDate(vacancyDto.closeDate());
        newVacancy.setFeatured(false);
        newVacancy.setStatus(VacancyStatus.PUBLISHED);
        newVacancy.setCategory(category);
        newVacancy.setDetails(vacancyDto.details());
        newVacancy.setWorkMode(WorkMode.ONSITE);
        newVacancy.setEmploymentType(EmploymentType.FULL_TIME);
        newVacancy.setCompany(company);
        newVacancy.setLocations(locations);
        newVacancy.setImage(vacancyDto.image());

        return newVacancy;
    }

    public Vacancy getVacancyById(Integer id) {
        return vacancyRepo.findById(id).orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));
    }

    public void deleteVacancy(Integer id) {
        Vacancy vacancy = getVacancyById(id);
        vacancyRepo.delete(vacancy);
    }

    // Applying filters & Pagination
    public Page<VacancyHomeDto> homeVacancies(String description, Integer categoryId,
                                              String workMode, Integer locationId, Pageable pageable) {
        Page<Vacancy> vacancies;

        // No Filter
        if (categoryId == null && description == null && locationId == null && workMode == null) {
            vacancies = vacancyRepo.findByFeaturedAndStatusOrderByIdAsc(true, VacancyStatus.OPEN, pageable);
        }
        else {
            vacancies = vacancyRepo.findAll(
                    Specification.where(VacancySpecification.hasDescription(description))
                            .and(VacancySpecification.hasCategory(categoryId))
                            .and(VacancySpecification.hasLocation(locationId))
                            .and(VacancySpecification.hasWorkMode(workMode)), pageable);
        }

        return vacancies.map(vacancy -> new VacancyHomeDto(
                vacancy.getId(),
                vacancy.getCategory().getName(),
                vacancy.getName(),
                vacancy.getPublishedDate(),
                vacancy.getStatus(),
                vacancy.getDescription()));
    }

    public VacancyDetailsDto showVacancyDetails(Integer id) {
        Vacancy vacancy = getVacancyById(id);
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
                ), vacancy.getLocations().stream().map(
                        location -> new LocationDto(location.getId(), location.getCity(), location.getState())).toList());
    }

}
