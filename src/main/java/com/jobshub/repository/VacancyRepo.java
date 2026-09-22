package com.jobshub.repository;

import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.VacancyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//imports to automatically change status
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface VacancyRepo extends JpaRepository<Vacancy, Integer>, JpaSpecificationExecutor<Vacancy> {

    boolean existsByNameIgnoreCaseAndCompany_Id(String name, Integer companyId);

    boolean existsByNameIgnoreCaseAndCompany_IdAndIdNot(String name, Integer companyId, Integer id);

    Page<Vacancy> findByFeaturedAndStatusOrderByIdAsc(boolean featured, VacancyStatus status, Pageable pageable);

    Page<Vacancy> findByStatusOrderByNameAsc(VacancyStatus status, Pageable pageable);

    // TODO: support multi-word search (currently matches exact phrase only — "software developer" won't match "developer" alone)
    @Query("""
    SELECT v FROM Vacancy v
    WHERE (:description is null or :description = '' or
         lower(v.name) like lower(concat('%', :description, '%') ) or
         lower(v.description) like lower(concat('%', :description, '%') ) )
    AND (:categoryId is null or v.category.id = :categoryId)""")
    Page<Vacancy> searchVacancies(@Param("description") String des, @Param("categoryId") Integer catId, Pageable pageable);




    @Modifying
    @Query("""
        update Vacancy v set v.status = :closedStatus where v.closeDate is not null and v.closeDate <= :today and v.status <> :closedStatus
        """)
    void closeExpiredVacancies(LocalDate today, VacancyStatus closedStatus);



}
