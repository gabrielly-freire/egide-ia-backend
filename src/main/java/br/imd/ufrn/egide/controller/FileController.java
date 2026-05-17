package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.FileDTO;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/files")
@Tag(name = "Arquivo", description = "Gerenciamento de arquivos")
// Controller de gerenciamento de arquivos de evidência vinculados às manifestações.
// Expõe endpoints de download, pré-visualização e listagem por manifestação.
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Download de arquivos")
    @ApiResponses(value = {
            @ApiResponse(description = "Download realizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Arquivo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        Resource resource = fileService.findResourceById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Listar provas (arquivos) de uma manifestação")
    @GetMapping("/by-report/{reportId}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<List<FileDTO>> listByReport(@PathVariable Long reportId) {
        List<FileDTO> files = fileService.findAllByReportId(reportId).stream()
                .map(f -> new FileDTO(f.getId(), f.getName(), f.getContentType(), f.getSize()))
                .toList();
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "Previsualização do arquivo")
    @ApiResponses(value = {
            @ApiResponse(description = "Previsualização realizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Arquivo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/preview/{id}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<Resource> preview(@PathVariable Long id) {

        FileEntity file = fileService.findById(id);
        Resource resource = fileService.findResourceById(id);

        return ResponseEntity.ok()
                .contentType(
                        file.getContentType() != null
                                ? MediaType.parseMediaType(file.getContentType())
                                : MediaType.APPLICATION_OCTET_STREAM
                )
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
