package com.cinemesh.movieservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.movieservice.application.dto.MovieDto;
import com.cinemesh.movieservice.application.dto.MovieGenreDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.movieservice.statics.MovieRated;
import com.cinemesh.movieservice.statics.MovieStatus;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.movieservice.domain.exception.MovieErrorCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Movie extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private String engTitle;
    private String vnTitle;
    private String description;
    private int durationMinutes;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String directors;
    private String actors;
    private MovieRated rated;
    private Set<MovieGenre> genres;
    private MovieStatus status;


    public Movie() {
        this.id = UUID.randomUUID();
        this.genres = new HashSet<>();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.MOVIE_CREATED, id));
    }

    public Movie(MovieDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.engTitle = dto.getEngTitle();
        this.vnTitle = dto.getVnTitle();
        this.description = dto.getDescription();
        this.durationMinutes = dto.getDurationMinutes();
        this.releaseDate = dto.getReleaseDate();
        this.posterUrl = dto.getPosterUrl();
        this.trailerUrl = dto.getTrailerUrl();
        this.directors = dto.getDirectors();
        this.actors = dto.getActors();
        this.rated = dto.getRated();
        this.status = dto.getStatus();
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            this.genres = dto.getGenres().stream()
                    .map(MovieGenre::new)
                    .collect(Collectors.toSet());
        } else {
            this.genres = new HashSet<>();
        }
        create();
    }

    public void update(MovieDto dto) {
        this.id = dto.getId();
        setEngTitle(dto.getEngTitle());
        setVnTitle(dto.getVnTitle());
        setDescription(dto.getDescription());
        setDurationMinutes(dto.getDurationMinutes());
        setReleaseDate(dto.getReleaseDate());
        setPosterUrl(dto.getPosterUrl());
        setTrailerUrl(dto.getTrailerUrl());
        setDirectors(dto.getDirectors());
        setActors(dto.getActors());
        setRated(dto.getRated());
        setStatus(dto.getStatus());
        setGenres(dto.getGenres());
        modify();
    }

    public void setEngTitle(String engTitle) {
        if (ObjectUtils.equals(this.engTitle, engTitle)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("engTitle", this.engTitle, engTitle)));
        this.engTitle = engTitle;
        modify();
    }

    public void setVnTitle(String vnTitle) {
        if (ObjectUtils.equals(this.vnTitle, vnTitle)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("vnTitle", this.vnTitle, vnTitle)));
        this.vnTitle = vnTitle;
        modify();
    }

    public void setDescription(String description) {
        if (ObjectUtils.equals(this.description, description)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("description", this.description, description)));
        this.description = description;
        modify();
    }

    public void setDurationMinutes(int durationMinutes) {
        if (ObjectUtils.equals(this.durationMinutes, durationMinutes)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("durationMinutes", this.durationMinutes, durationMinutes)));
        this.durationMinutes = durationMinutes;
        modify();
    }

    public void setReleaseDate(LocalDate releaseDate) {
        if (ObjectUtils.equals(this.releaseDate, releaseDate)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("releaseDate", this.releaseDate, releaseDate)));
        this.releaseDate = releaseDate;
        modify();
    }

    public void setPosterUrl(String posterUrl) {
        if (ObjectUtils.equals(this.posterUrl, posterUrl)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("posterUrl", this.posterUrl, posterUrl)));
        this.posterUrl = posterUrl;
        modify();
    }

    public void setTrailerUrl(String trailerUrl) {
        if (ObjectUtils.equals(this.trailerUrl, trailerUrl)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("trailerUrl", this.trailerUrl, trailerUrl)));
        this.trailerUrl = trailerUrl;
        modify();
    }

    public void setDirectors(String directors) {
        if (ObjectUtils.equals(this.directors, directors)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("directors", this.directors, directors)));
        this.directors = directors;
        modify();
    }

    public void setActors(String actors) {
        if (ObjectUtils.equals(this.actors, actors)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("actors", this.actors, actors)));
        this.actors = actors;
        modify();
    }

    public void setRated(MovieRated rated) {
        if (ObjectUtils.equals(this.rated, rated)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("rated", this.rated, rated)));
        this.rated = rated;
        modify();
    }

    public void setStatus(MovieStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }

    /**
     * Nhận 1 list DTO:
     * - Phần tử nào không có ID -> bắn lỗi, vì genre phải tồn tại trước đó rồi
     * - Phan tử nào tồn tại ID -> update bình thường
     */
    public void setGenres(Collection<MovieGenreDto> dtos) {
        this.genres = this.genres == null ? new HashSet<>() : this.genres;

        // danh sách phần tử không truyền lên id
        List<MovieGenreDto> noIdGenres = dtos.stream().filter(x -> x.getId() == null).toList();

        if (!noIdGenres.isEmpty()) {
            throw new UnprocessableEntityException(MovieErrorCode.GENRE_MUST_BE_EXISTED_BEFORE_CREATE_MOVIE);
        }
        this.genres = dtos.stream()
                .map(MovieGenre::new)
                .collect(Collectors.toSet());
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("genres", this.genres, genres)));
        modify();
    }

}
