package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.GeneralValidationAlterRequestDTO;
import br.imd.ufrn.egide.dto.GeneralValidationResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorGeralCaseDTO;
import br.imd.ufrn.egide.service.GeneralValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controller responsável pelas operações da Fase 4 do fluxo de Ouvidoria.
// Expõe endpoints exclusivos para o Ouvidor Geral (GENERAL_LISTENER) e ADMIN,
// permitindo consulta da fila de casos pendentes e execução das três ações disponíveis:
//   - Validar: confirma o relatório como está.
//   - Alterar: substitui a decisão/penalidade do ouvidor pela da OG.
//   - Repassar: designa novo ouvidor imparcial (máximo 1 vez por caso — regra de não-loop).
// Toda lógica de negócio é delegada ao GeneralValidationServiceImpl;
// este controller é responsável apenas pelo roteamento e validação de entrada.
@AllArgsConstructor
@RestController
@RequestMapping("/v1/ouvidor-geral")
@Tag(name = "Ouvidor Geral", description = "Validação, alteração e repass de relatórios")
public class OuvidorGeralController {

    private final GeneralValidationService generalValidationService;

    @GetMapping("/casos")
    @PreAuthorize("hasAnyRole('GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Lista os casos pendentes de validação do Ouvidor Geral")
    public ResponseEntity<List<OuvidorGeralCaseDTO>> listPendingCases() {
        return ResponseEntity.ok(generalValidationService.findPendingCases());
    }

    @PostMapping("/relatorio-final/{reportId}/validar")
    @PreAuthorize("hasAnyRole('GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Valida o relatório (mantém o parecer como está)")
    public ResponseEntity<GeneralValidationResponseDTO> validate(@PathVariable Long reportId) {
        return ResponseEntity.ok(generalValidationService.validate(reportId));
    }

    @PostMapping("/relatorio-final/{reportId}/alterar")
    @PreAuthorize("hasAnyRole('GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Altera o parecer do relatório (registra novo, preservando o original)")
    public ResponseEntity<GeneralValidationResponseDTO> alter(
            @PathVariable Long reportId,
            @Valid @RequestBody GeneralValidationAlterRequestDTO request) {
        return ResponseEntity.ok(generalValidationService.alter(reportId, request));
    }

    @PostMapping("/relatorio-final/{reportId}/repassar")
    @PreAuthorize("hasAnyRole('GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Repassa o caso para um novo ouvidor (regra de não-loop: máximo 1 vez)")
    public ResponseEntity<GeneralValidationResponseDTO> repass(@PathVariable Long reportId) {
        return ResponseEntity.ok(generalValidationService.repass(reportId));
    }
}
