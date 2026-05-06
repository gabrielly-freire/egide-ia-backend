package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SatisfactionSurveyRequestDTO(
        @NotNull Long reportId,
        @Min(0) @Max(5) Integer speedRating,
        @Min(0) @Max(5) Integer resolutionRating,
        String comments
) {}
