package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Paginated response wrapper")
public class PagedResponseDto<T> {

    @Schema(description = "Page content")
    private List<T> content;

    @Schema(description = "Total number of elements across all pages", example = "42")
    private long totalElements;

    @Schema(description = "Total number of pages available", example = "5")
    private int totalPages;

    @Schema(description = "Current page number (0-based)", example = "0")
    private int currentPage;

    @Schema(description = "Number of elements per page", example = "10")
    private int pageSize;
}
