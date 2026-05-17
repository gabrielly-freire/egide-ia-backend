package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/user-info")
@Tag(name = "Usuário", description = "Gerenciamento de usuários")
// Controller de gerenciamento de usuários do sistema; operações de escrita restritas a ADMIN.
// Leitura (listagem e consulta) permitida a MANAGER e ADMIN.
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "Criação de um usuário")
    @ApiResponses(value = {
            @ApiResponse(description = "Usuário criado com sucesso.", responseCode = "201"),
            @ApiResponse(description = "Requisição mal formatada", responseCode = "400"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> create(@Valid @RequestBody UserInfoDTO userInfo) {
        return ResponseEntity.ok(userInfoService.save(userInfo));
    }

    @Operation(summary = "Listagem de usuários")
    @ApiResponses(value = {
            @ApiResponse(description = "Listagem exibida com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Page<UserInfoDTO>> list(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(userInfoService.list(pageable));
    }

    @Operation(summary = "Encontrar um usuário pelo id")
    @ApiResponses(value = {
            @ApiResponse(description = "Usuário encontrado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Usuário não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<UserInfoDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(userInfoService.get(id));
    }

    @Operation(summary = "Atualização de usuário")
    @ApiResponses(value = {
            @ApiResponse(description = "Usuário atualizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Requisição mal formatada.", responseCode = "400"),
            @ApiResponse(description = "Usuário não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> update(@PathVariable Long id, @Valid @RequestBody UserInfoDTO userInfo) {
        return ResponseEntity.ok(userInfoService.update(id, userInfo));
    }

    @Operation(summary = "Exclusão do usuário")
    @ApiResponses(value = {
            @ApiResponse(description = "Exclusão realizada com sucesso.", responseCode = "204"),
            @ApiResponse(description = "Usuário não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Não foi possível realizar a exclusão devido a lógica negocial", responseCode = "409"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id) {
        userInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
