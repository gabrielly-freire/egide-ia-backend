package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import br.imd.ufrn.egide.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/departments")
@Tag(name = "Departamento", description = "Consulta de departamentos")
// Controller de consulta de departamentos institucionais; acessível a todos os papéis autenticados.
// Expõe apenas leitura (GET); criação e edição de departamentos são operações administrativas externas.
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "Listagem de departamentos")
    @ApiResponses(value = {
            @ApiResponse(description = "Listagem exibida com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<Page<DepartmentDTO>> list(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(departmentService.list(pageable));
    }

    @Operation(summary = "Encontrar um departamento pelo id")
    @ApiResponses(value = {
            @ApiResponse(description = "Departamento encontrado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Departamento não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','MANAGER','ADMIN')")
    public ResponseEntity<DepartmentDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.get(id));
    }
}
