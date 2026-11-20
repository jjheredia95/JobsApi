package com.jobshub.repository;

import com.jobshub.dto.vacancy.VacancyCardDto;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.VacancyStatus;
import com.jobshub.model.enums.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//imports to automatically change status
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface VacancyRepo extends JpaRepository<Vacancy, Integer>, JpaSpecificationExecutor<Vacancy> {

    long countByStatus(VacancyStatus status);
    boolean existsByNameIgnoreCaseAndCompany_Id(String name, Integer companyId);

    // FEATURED VACANCIES (ALWAYS OPEN OR PUBLISHED, OPEN COMES FIRST)
    @Query("""
    SELECT v FROM Vacancy v
    WHERE v.featured = true AND v.status IN (com.jobshub.model.enums.VacancyStatus.OPEN, com.jobshub.model.enums.VacancyStatus.PUBLISHED)
    ORDER BY
      CASE WHEN v.status = com.jobshub.model.enums.VacancyStatus.OPEN THEN 0 ELSE 1 END,
      v.id ASC
    """)
    Page<Vacancy> findFeaturedOpenPublishedVacancies(Pageable pageable);










    // TODO: support multi-word search (currently matches exact phrase only — "software developer" won't match "developer" alone)
    @Query("""
    SELECT v FROM Vacancy v
    WHERE (
         cast(:keyword as String) is null
         or lower(v.name) like concat('%', cast(:keyword as String), '%')
         or lower(v.description) like concat('%', cast(:keyword as String), '%'))
    AND (:categoryId is null or v.category.id = :categoryId)
    AND (:state is null or exists (select l from v.locations l where l.state = :state))
    AND (:employment_type is null or v.employmentType = :employment_type)
    AND (:workMode is null or v.workMode = :workMode)
    AND (v.status in :statuses)
    ORDER BY v.name, v.id
    """)
    Page<Vacancy> searchVacanciesByFiltering(@Param("keyword") String keyword,
                                                    @Param("categoryId") Integer category,
                                                    @Param("state") String state,
                                                    @Param("employment_type") EmploymentType type,
                                                    @Param("workMode") WorkMode mode,
                                                    @Param ("statuses") List<VacancyStatus> statuses,
                                                    Pageable pageable);

    // ADMIN SEARCH
    @Query("""
    select v from Vacancy v
         where
             (:name is null
             or lower(v.name) like lower(concat('%', :name, '%') )
             or lower(v.description) like lower(concat('%', :name, '%') ) )
         and(
             :categoryId is null
             or v.category.id = :categoryId
             )
         and (
             :companyId is null
             or v.company.id = :companyId
             )
         and (:status is null
             or v.status = :status)""")
    Page<Vacancy> searchAdminVacancies(
            @Param("name") String name,
            @Param("categoryId") Integer categoryId,
            @Param("companyId") Integer companyId,
            @Param("status") VacancyStatus status,
            Pageable pageable);

    // Automatically close open vacancies which close date has arrived
    @Modifying(clearAutomatically = true)
    @Query("""
       UPDATE Vacancy v
       SET v.status = :closed
       WHERE v.status = :open
         AND v.closeDate < :today
       """)
    void closeExpiredVacancies(@Param("open") VacancyStatus open,
                              @Param("closed") VacancyStatus closed,
                              @Param("today") LocalDate today);





}
