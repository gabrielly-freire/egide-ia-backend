package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/report")
@Tag(name = "Denúncia", description = "Gerenciamento de denúncias")
public class ReportController {

    private final ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar uma nova denúncia")
    public ResponseEntity<ReportDTO> create(
            @Valid @RequestPart("report") ReportRequestDTO reportRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ResponseEntity.ok(reportService.save(reportRequestDTO, files));
    }

    @GetMapping
    @Operation(summary = "Listar todas as denúncias")
    public ResponseEntity<List<ReportDTO>> listAll() {
        return ResponseEntity.ok(reportService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar denúncia por ID")
    public ResponseEntity<ReportDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

}
