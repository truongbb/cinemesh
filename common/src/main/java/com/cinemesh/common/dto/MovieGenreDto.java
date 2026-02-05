package com.cinemesh.common.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieGenreDto {

    UUID id;
    String name;

}
