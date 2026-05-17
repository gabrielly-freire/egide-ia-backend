package br.imd.ufrn.egide.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// DTO de resposta padronizado para erros da API.
// Retornado pelo HandlerGlobalException em todas as exceções tratadas.
// O campo errors é utilizado exclusivamente em erros de validação (400), listando os campos inválidos.
public class ErrorMessage {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String description;
    private List<String> errors;
}
