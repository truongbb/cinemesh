package com.cinemesh.movieservice.infrastructure.persistence.specification;

import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import org.springframework.data.jpa.domain.Specification;

public class MovieGenreSpecification {

    public static Specification<MovieGenreEntity> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

}
