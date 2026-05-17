package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

// DTO de entrada para o ouvidor registrar uma observação analítica sobre um arquivo de prova.
// O fileId e o reportId são recebidos como path variables no endpoint, não neste record.
// A observação não pode ser vazia: a anotação @NotBlank garante validação na camada de entrada,
// evitando registros sem conteúdo que poluiriam o histórico de análise do caso.
public record ProofObservationRequestDTO(
        @NotBlank(message = "A observação é obrigatória")
        String observation
) {
}
