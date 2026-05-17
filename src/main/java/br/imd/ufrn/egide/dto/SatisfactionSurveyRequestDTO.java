package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// DTO de criação de pesquisa de satisfação vinculada a uma manifestação encerrada.
// speedRating e resolutionRating aceitam valores de 0 a 5; a validação é feita via @Min/@Max.
// Regra de negócio: apenas uma pesquisa por manifestação é permitida (verificação no ReportServiceImpl).
public record SatisfactionSurveyRequestDTO(
        @NotNull Long reportId,
        @Min(0) @Max(5) Integer speedRating,
        @Min(0) @Max(5) Integer resolutionRating,
        String comments
) { }
