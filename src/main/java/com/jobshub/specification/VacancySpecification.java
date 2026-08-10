package com.jobshub.specification;

import com.jobshub.model.Location;
import com.jobshub.model.Vacancy;
import com.jobshub.model.enums.EmploymentType;
import com.jobshub.model.enums.WorkMode;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VacancySpecification {

    public static Specification<Vacancy> hasCategory (Integer categoryId) {
        return ((root, query, criteriaBuilder) -> categoryId == null ? null:
                criteriaBuilder.equal(
                        root.get("category").get("id"), categoryId));
    }

    public static Specification<Vacancy> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;

            String[] tokens = search.toLowerCase().trim().split("\\s+");
            List<Predicate> tokenPredicates = new ArrayList<>();

            for (int i = 0; i < Math.min(tokens.length, 5); i++) {
                String pattern = "%" + tokens[i] + "%";
                tokenPredicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(tokenPredicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Vacancy> hasLocation(Integer locationId) {
        return (root, query, cb) -> {
            if (locationId == null) return null;
            Join<Vacancy, Location> locationJoin = root.join("locations", JoinType.INNER);
            return cb.equal(locationJoin.get("id"), locationId);
        };
    }

    public static Specification<Vacancy> hasWorkMode(WorkMode workMode) {
        return (root, query, cb) -> workMode == null ? null :
                cb.equal(root.get("workMode"), workMode);
    }

    /*public static Specification<Vacancy> hasCompany(Integer companyId) {
        return ((root, query, criteriaBuilder) -> companyId == null ? null :
                criteriaBuilder.equal(
                        root.get("company").get("id"), companyId));
    }*/

    public static Specification<Vacancy> hasEmploymentType(EmploymentType employmentType) {
        return (root, query, cb) -> employmentType == null ? null :
                cb.equal(root.get("employmentType"), employmentType);
    }

}
