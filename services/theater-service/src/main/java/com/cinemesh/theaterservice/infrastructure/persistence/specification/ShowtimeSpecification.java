package com.cinemesh.theaterservice.infrastructure.persistence.specification;

import com.cinemesh.theaterservice.application.dto.request.ShowtimeSearchRequest;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeSpecification {

    private ShowtimeSpecification() {
    }

    public static Specification<ShowTimeEntity> search(ShowtimeSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getMovieId() != null) {
                predicates.add(cb.equal(root.get("movieId"), request.getMovieId()));
            }

            if (request.getShowingDate() != null) {
                LocalDateTime startDate = request.getShowingDate().atStartOfDay();
                LocalDateTime endDate = request.getShowingDate().atTime(23, 59, 59);
                predicates.add(cb.between(root.get("startTime"), startDate, endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
