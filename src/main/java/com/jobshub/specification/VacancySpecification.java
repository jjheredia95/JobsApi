package com.jobshub.specification;

import com.jobshub.model.Location;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.WorkMode;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class VacancySpecification {

    public static Specification<Vacancy> hasCategory (Integer categoryId) {
        return ((root, query, criteriaBuilder) -> categoryId == null ? null:
                criteriaBuilder.equal(
                        root.get("category").get("id"), categoryId));
    }

    public static Specification<Vacancy> hasDescription(String description) {
        return ((root, query, criteriaBuilder) -> description == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(
                        root.get("description")), "%" + description.toLowerCase() + "%"));
    }

    public static Specification<Vacancy> hasLocation(Integer locationId) {
        return (root, query, cb) -> {
            if (locationId == null) return null;
            Join<Vacancy, Location> locationJoin = root.join("locations", JoinType.INNER);
            return cb.equal(locationJoin.get("id"), locationId);
        };
    }

    public static Specification<Vacancy> hasWorkMode(String workMode) {
        return (root, query, cb) -> workMode == null ? null :
                cb.equal(root.get("workMode"), WorkMode.valueOf(workMode.toUpperCase()));
    }

}
