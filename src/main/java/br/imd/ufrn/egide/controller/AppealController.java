package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.AppealRequestDTO;
import br.imd.ufrn.egide.dto.AppealResponseDTO;
import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorCaseDTO;
import br.imd.ufrn.egide.service.AppealService;
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

// Controller responsável pelas operações da Fase 5 do fluxo de Ouvidoria.
// Agrupa dois perfis de usuário:
//   - REMONSTRANT: pode abrir recurso após GENERAL_VALIDATED.
//   - LISTENER: novo ouvidor consulta os casos atribuídos a ele e emite o AppealReport.
// Toda a lógica de negócio (anti-viés, regra de merge, unicidade por parte) é delegada
// ao AppealServiceImpl; este controller é responsável apenas pelo roteamento HTTP.
// Nota: o path base /v1 sem prefixo específico é usado para os endpoints de submissão de recurso
// e listagem por manifestação, enquanto /recurso agrupa os endpoints do novo ouvidor.
@AllArgsConstructor
@RestController
@RequestMapping("/v1")
@Tag(name = "Recurso", description = "Fase 5 — recursos e análise pelo novo ouvidor")
public class AppealController {

    private final AppealService appealService;

    @PostMapping("/report/{id}/recurso")
    @PreAuthorize("hasAnyRole('REMONSTRANT','ADMIN')")
    @Operation(summary = "Abre um recurso para o caso (denunciante ou denunciado)")
    public ResponseEntity<AppealResponseDTO> submit(
            @PathVariable Long id,
            @Valid @RequestBody AppealRequestDTO request) {
        return ResponseEntity.ok(appealService.submit(id, request));
    }

    @GetMapping("/report/{id}/recursos")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Lista os recursos abertos para um caso")
    public ResponseEntity<List<AppealResponseDTO>> listByReport(@PathVariable Long id) {
        return ResponseEntity.ok(appealService.listByReport(id));
    }

    @GetMapping("/recurso/ouvidor/casos")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Lista recursos atribuídos ao Ouvidor autenticado (anti-viés: sem relatórios anteriores)")
    public ResponseEntity<List<OuvidorCaseDTO>> listAssigned() {
        return ResponseEntity.ok(appealService.findAppealCasesAssignedToCurrentOuvidor());
    }

    @PostMapping("/recurso/report/{id}/relatorio")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Submete o relatório do recurso (novo ouvidor) — caso passa para validação da OG")
    public ResponseEntity<FinalReportResponseDTO> submitAppealReport(
            @PathVariable Long id,
            @Valid @RequestBody FinalReportRequestDTO request) {
        return ResponseEntity.ok(appealService.submitAppealReport(id, request));
    }
}
