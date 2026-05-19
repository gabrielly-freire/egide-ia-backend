package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.*;
import br.imd.ufrn.egide.service.DefenseService;
import br.imd.ufrn.egide.service.FinalReportService;
import br.imd.ufrn.egide.service.PreliminaryReportService;
import br.imd.ufrn.egide.service.ProofObservationService;
import br.imd.ufrn.egide.service.ReportExportService;
import br.imd.ufrn.egide.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import br.imd.ufrn.egide.dto.ReportDTO;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/report")
@Tag(name = "Denúncia", description = "Gerenciamento de denúncias")
public class ReportController {

    private final ReportService reportService;
    private final DefenseService defenseService;
    private final FinalReportService finalReportService;
    private final PreliminaryReportService preliminaryReportService;
    private final ProofObservationService proofObservationService;
    private final ReportExportService exportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('REMONSTRANT','ADMIN')")
    @Operation(summary = "Criar uma nova denúncia")
    public ResponseEntity<ReportDTO> create(
            @Valid @RequestPart("report") ReportRequestDTO reportRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ResponseEntity.ok(reportService.save(reportRequestDTO, files));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Listar todas as denúncias visíveis ao usuário autenticado")
    public ResponseEntity<List<ReportDTO>> listAll() {
        return ResponseEntity.ok(reportService.findAll());
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Listar manifestações do denunciante autenticado")
    public ResponseEntity<List<ReportDTO>> findMyReports() {
        return ResponseEntity.ok(reportService.findMyReports());
    }

    @GetMapping("/ouvidor/casos")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Listar casos atribuídos ao Ouvidor autenticado")
    public ResponseEntity<List<OuvidorCaseDTO>> listCasesAssignedToCurrentOuvidor() {
        return ResponseEntity.ok(reportService.findCasesAssignedToCurrentOuvidor());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','GENERAL_LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Buscar denúncia por ID")
    public ResponseEntity<ReportDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

    @GetMapping("/dashboard/status")
    @Operation(summary = "Métricas para o painel de gestão")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStatus() {
        return ResponseEntity.ok(reportService.getDashboardStatus());
    }

    @GetMapping("/{id}/sugerir-resposta")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Sugerir resposta para uma denúncia (IA)")
    public ResponseEntity<ReportResponseSuggestionResponseDTO> suggestResponse(@PathVariable Long id) {
        return ResponseEntity.ok(preliminaryReportService.suggestResponse(id));
    }

    @GetMapping("/{id}/parecer-preliminar")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Recuperar o parecer preliminar emitido pelo Ouvidor")
    public ResponseEntity<PreliminaryReportResponseDTO> getPreliminaryReport(@PathVariable Long id) {
        return ResponseEntity.ok(preliminaryReportService.getByReportId(id));
    }

    @PostMapping("/{id}/parecer-preliminar")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Submeter parecer preliminar (acatar, negar ou encerrar por falta de provas)")
    public ResponseEntity<PreliminaryReportResponseDTO> submitPreliminaryReport(
            @PathVariable Long id,
            @Valid @RequestBody PreliminaryReportRequestDTO request) {
        return ResponseEntity.ok(preliminaryReportService.submit(id, request));
    }

    @GetMapping("/{id}/observacoes")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Listar observações registradas pelo Ouvidor nas provas")
    public ResponseEntity<List<ProofObservationResponseDTO>> listProofObservations(@PathVariable Long id) {
        return ResponseEntity.ok(proofObservationService.listByReport(id));
    }

    @PostMapping("/{id}/provas/{fileId}/observacao")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Registrar/atualizar observação do Ouvidor sobre uma prova")
    public ResponseEntity<ProofObservationResponseDTO> upsertProofObservation(
            @PathVariable Long id,
            @PathVariable Long fileId,
            @Valid @RequestBody ProofObservationRequestDTO request) {
        return ResponseEntity.ok(proofObservationService.upsert(id, fileId, request));
    }

    @PostMapping("/{id}/relatorio-final")
    @PreAuthorize("hasAnyRole('LISTENER','ADMIN')")
    @Operation(summary = "Submete o relatório final (mesmo ouvidor da Fase 2 após análise da defesa)")
    public ResponseEntity<FinalReportResponseDTO> submitFinalReport(
            @PathVariable Long id,
            @Valid @RequestBody FinalReportRequestDTO request) {
        return ResponseEntity.ok(finalReportService.submit(id, request));
    }

    @GetMapping("/{id}/relatorio-final")
    @PreAuthorize("hasAnyRole('LISTENER','GENERAL_LISTENER','ADMIN')")
    @Operation(summary = "Recupera o relatório final do caso")
    public ResponseEntity<FinalReportResponseDTO> getFinalReport(@PathVariable Long id) {
        return ResponseEntity.ok(finalReportService.getByReportId(id));
    }

    @PostMapping("/{id}/survey")
    @Operation(summary = "Enviar pesquisa de satisfação para uma denúncia")
    public ResponseEntity<Void> submitSurvey(@PathVariable Long id, @Valid @RequestBody SatisfactionSurveyRequestDTO surveyDTO) {
        reportService.saveSurvey(id, surveyDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/defesa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Enviar defesa do denunciado")
    public ResponseEntity<DefenseDTO> submitDefense(
            @PathVariable Long id,
            @Valid @RequestPart("defense") DefenseRequestDTO defenseRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(defenseService.submitDefense(id, defenseRequestDTO, files));
    }

    @GetMapping("/{id}/defesa")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Buscar defesa de um caso")
    public ResponseEntity<DefenseDTO> getDefense(@PathVariable Long id) {
        return ResponseEntity.ok(defenseService.getDefense(id));
    }

    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasRole('GENERAL_LISTENER')")
    @Operation(summary = "Conclui uma manifestação (Acesso exclusivo ao Ouvidor Geral)")
    public ResponseEntity<ReportDTO> concluirRelato(@PathVariable Long id) {
        ReportDTO response = reportService.concluirRelato(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/exportar")
    @Operation(summary = "Gera o PDF consolidado da manifestação")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) {
        byte[] pdf = exportService.generateReportPdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=relatorio-" + id + ".pdf")
                .body(pdf);
    }

    @GetMapping("/exportar-governanca")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Gera o PDF de governança com métricas consolidadas do sistema")
    public ResponseEntity<byte[]> exportarGovernanca() {
        byte[] pdf = exportService.generateGovernancePdf();
        String filename = "governanca-" + java.time.LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(pdf);
    }
}