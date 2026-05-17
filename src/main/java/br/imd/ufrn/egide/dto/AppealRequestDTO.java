package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.AppellantRole;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada para abertura de recurso por uma das partes na Fase 5.
// O campo appellantRole é opcional na maioria dos casos: o AppealServiceImpl infere
// o papel do usuário autenticado com base no vínculo com a manifestação
// (denunciante → DENUNCIANTE, denunciado → DENUNCIADO).
// É necessário apenas quando o ADMIN abre recurso em nome de uma das partes,
// situação em que o service não consegue inferir o papel automaticamente.
// grounds é obrigatório: o recurso deve ter fundamentos explícitos para ser válido formalmente.
public record AppealRequestDTO(
        AppellantRole appellantRole,

        @NotBlank(message = "Os fundamentos do recurso são obrigatórios")
        String grounds
) {
}
