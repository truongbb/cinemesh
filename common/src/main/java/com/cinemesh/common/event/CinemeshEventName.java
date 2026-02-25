package com.cinemesh.common.event;

public enum CinemeshEventName {
    FIELD_VALUE_CHANGED,

    /**
     * AUTH-SERVICE
     */
    USER_CREATED,
    USER_ROLES_ADDED,
    USER_ROLES_UPDATED,
    USER_ROLES_REMOVED,

    ROLE_CREATED,
    ROLE_UPDATED,
    ROLE_REMOVED,

    USER_ACTIVATED,
    USER_DEACTIVATED,

    /**
     * MOVIE-SERVICE
     */
    MOVIE_CREATED,

    GENRE_CREATED,
    GENRE_UPDATED,

    /**
     * THEATER-SERVICE
     */
    ROOM_CREATED,

    SEAT_ADDED,
    SEAT_UPDATED,
    SEAT_REMOVED,

    SHOWTIME_CREATED,

    /**
     * BOOKING-SERVICE
     */
    TICKET_ADDED,
    TICKET_UPDATED,
    TICKET_REMOVED,

    ORDER_CREATED,
}
