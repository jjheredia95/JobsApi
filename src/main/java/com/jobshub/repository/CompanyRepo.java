package com.jobshub.repository;

import com.jobshub.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Integer> {

    Company findByNameIgnoreCase(String name);

}
