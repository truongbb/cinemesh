package com.cinemesh.theaterservice.infrastructure.persistence.specification;

import com.cinemesh.theaterservice.application.dto.request.RoomSearchRequest;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {

    private RoomSpecification() {
    }

    public static Specification<RoomEntity> search(RoomSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null && !request.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(root.get("status").in(request.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
