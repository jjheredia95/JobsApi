package com.jobshub.repository;

import com.jobshub.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LocationRepo extends JpaRepository<Location, Integer> {

    boolean existsByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
}
