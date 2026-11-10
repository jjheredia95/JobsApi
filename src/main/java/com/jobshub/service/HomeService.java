package com.jobshub.service;

import com.jobshub.dto.home.HomeStatDto;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.repository.CategoryRepo;
import com.jobshub.repository.CompanyRepo;
import com.jobshub.repository.LocationRepo;
import com.jobshub.repository.VacancyRepo;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final VacancyRepo vacancyRepo;
    private final CategoryRepo categoryRepo;
    private final CompanyRepo companyRepo;
    private final LocationRepo locationRepo;

    public HomeService(VacancyRepo vacancyRepo,
                          CategoryRepo categoryRepo,
                          CompanyRepo companyRepo,
                          LocationRepo locationRepo) {
        this.vacancyRepo = vacancyRepo;
        this.categoryRepo = categoryRepo;
        this.companyRepo = companyRepo;
        this.locationRepo = locationRepo;
    }

    // SHOW HOME STATISTICS
    public HomeStatDto showHomeStats() {
        long jobsOpen = vacancyRepo.countByStatus(VacancyStatus.OPEN);
        long companiesCount = companyRepo.count();
        long categoriesCount = categoryRepo.count();
        long citiesCount = locationRepo.count();
        return new HomeStatDto(jobsOpen, companiesCount, categoriesCount, citiesCount);
    }







}
