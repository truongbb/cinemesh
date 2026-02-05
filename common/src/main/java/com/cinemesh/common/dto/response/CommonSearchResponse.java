package com.cinemesh.common.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonSearchResponse<T> {

    List<T> data;

    PaginationResponse pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaginationResponse {
        long totalRecords;
        int totalPage;
        int pageIndex;
        int pageSize;
    }

}
