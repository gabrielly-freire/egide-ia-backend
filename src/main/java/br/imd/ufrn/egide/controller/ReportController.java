package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.*;
import br.imd.ufrn.egide.service.ReportService;
import br.imd.ufrn.egide.service.ReportResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/report")
@Tag(name = "Denúncia", description = "Gerenciamento de denúncias")
public class ReportController {

    private final ReportService reportService;
    private final ReportResponseService reportResponseService;

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
    @Operation(summary = "Listar todas as denúncias")
    public ResponseEntity<List<ReportDTO>> listAll() {
        return ResponseEntity.ok(reportService.findAll());
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<ReportDTO>> findMyReports() {
        return ResponseEntity.ok(reportService.findMyReports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
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
    @PreAuthorize("hasAnyRole('LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Sugerir resposta para uma denúncia (IA)")
    public ResponseEntity<ReportResponseSuggestionResponseDTO> suggestResponse(@PathVariable Long id) {
        return ResponseEntity.ok(reportResponseService.suggestResponse(id));
    }

    @GetMapping("/{id}/resposta")
    @PreAuthorize("hasAnyRole('LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Buscar resposta registrada para uma denúncia")
    public ResponseEntity<ReportRespondResponseDTO> getResponse(@PathVariable Long id) {
        return ResponseEntity.ok(reportResponseService.getResponse(id));
    }

    @PostMapping("/{id}/responder")
    @PreAuthorize("hasAnyRole('LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Responder uma denúncia considerando a sugestão da IA")
    public ResponseEntity<ReportRespondResponseDTO> respond(
            @PathVariable Long id,
            @RequestBody(required = false) ReportRespondRequestDTO request
    ) {
        return ResponseEntity.ok(reportResponseService.respond(id, request));
    }

    @PostMapping("/{id}/survey")
    @Operation(summary = "Enviar pesquisa de satisfação para uma denúncia")
    public ResponseEntity<Void> submitSurvey(@PathVariable Long id, @Valid @RequestBody SatisfactionSurveyRequestDTO surveyDTO) {
        reportService.saveSurvey(id, surveyDTO);
        return ResponseEntity.ok().build();
    }

}
