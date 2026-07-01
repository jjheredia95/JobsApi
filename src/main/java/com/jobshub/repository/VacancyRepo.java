package com.jobshub.repository;

import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.VacancyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VacancyRepo extends JpaRepository<Vacancy, Integer>, JpaSpecificationExecutor<Vacancy> {

    boolean existsByNameIgnoreCaseAndCompany_Id(String name, Integer companyId);

    boolean existsByNameIgnoreCaseAndCompany_IdAndIdNot(String name, Integer companyId, Integer id);

    Page<Vacancy> findByFeaturedAndStatusOrderByIdAsc(boolean featured, VacancyStatus status, Pageable pageable);



}
