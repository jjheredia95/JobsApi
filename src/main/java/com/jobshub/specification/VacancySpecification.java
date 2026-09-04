package com.jobshub.specification;

import com.jobshub.model.Vacancy;
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

    public static Specification<Vacancy> hasDescription(String search) {
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

}
