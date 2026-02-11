package com.cinemesh.movieservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.MovieStatus;
import com.cinemesh.common.statics.MovieRated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity extends BaseEntity {

    @Column(name = "title_en")
    private String engTitle;

    @Column(name = "title_vn")
    private String vnTitle;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private int durationMinutes;

    @Column
    private LocalDate releaseDate;

    @Column(length = 1000)
    private String posterUrl;

    @Column(length = 1000)
    private String trailerUrl;

    @Column(length = 1000)
    private String directors;

    @Column(length = 1000)
    private String actors;

    @Column(columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private MovieRated rated;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_genre_id")
    )
    private Set<MovieGenreEntity> genres = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column
    private MovieStatus status;


}
