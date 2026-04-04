package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.service.ReportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/report")
@Tag(name = "Denúncia", description = "Gerenciamento de denúncias")
public class ReportController {

    private final ReportServiceImpl reportService;

    @PostMapping
    @Operation(summary = "Criar uma nova denúncia")
    public ResponseEntity<ReportDTO> create(@Valid @RequestBody ReportDTO reportDTO) {
        return ResponseEntity.ok(reportService.save(reportDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar denúncia por ID")
    public ResponseEntity<ReportEntity> get(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }
}