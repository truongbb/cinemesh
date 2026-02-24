package com.cinemesh.movieservice.infrastructure.persistence.specification;

import com.cinemesh.movieservice.application.dto.request.SearchMovieRequest;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecification {

    private MovieSpecification() {
    }

    public static Specification<MovieEntity> search(SearchMovieRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Text Search (Partial Match)
            if (request.getEngTitle() != null && !request.getEngTitle().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("engTitle")),
                        "%" + request.getEngTitle().toLowerCase() + "%"));
            }
            if (request.getVnTitle() != null && !request.getVnTitle().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("vnTitle")),
                        "%" + request.getVnTitle().toLowerCase() + "%"));
            }

            // 2. Date Range
            if (request.getReleaseDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("releaseDate"),
                        request.getReleaseDateFrom()));
            }
            if (request.getReleaseDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"),
                        request.getReleaseDateTo()));
            }

            // 3. ENUM Lists (IN Clause)
            if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(request.getStatuses()));
            }
            if (request.getRated() != null && !request.getRated().isEmpty()) {
                predicates.add(root.get("rated").in(request.getRated()));
            }

            // 4. RELATIONSHIPS (The Tricky Part)
            // Search Movies that have ANY of these Genre IDs
            if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
                // Joins "genres" table and checks IDs
                predicates.add(root.join("genres").get("id").in(request.getGenreIds()));

                // CRITICAL: Prevent duplicate movies when joining
                query.distinct(true);
            }

            // 5. Search by Director/Actor Name (Assuming they are related Entities)
            // If they are just Strings in DB, use .like() as above.
            // If they are Entities, join them:
            if (request.getDirectors() != null && !request.getDirectors().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("directors").get("name")),
                        "%" + request.getDirectors().toLowerCase() + "%"));
            }

            if (!CollectionUtils.isEmpty(request.getIds())) {
                predicates.add(root.get("id").in(request.getIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
