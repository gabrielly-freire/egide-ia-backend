package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/files")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Upload de arquivos")
    @ApiResponses(value = {
            @ApiResponse(description = "Upload realizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Arquivo vazio ou maior que o tamanho definido.", responseCode = "400"),
            @ApiResponse(description = "Tipo de arquivo não suportado", responseCode = "415"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500"),

    })
    @PostMapping("/upload")
    public ResponseEntity upload(
            @RequestParam("file") List<MultipartFile> file) {
        fileService.upload(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Download de arquivos")
    @ApiResponses(value = {
            @ApiResponse(description = "Download realizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Arquivo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        Resource resource = fileService.findResourceById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Previsualização do arquivo")
    @ApiResponses(value = {
            @ApiResponse(description = "Previsualização realizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Arquivo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/preview/{id}")
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