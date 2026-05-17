package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.AuthenticatedUserDTO;
import br.imd.ufrn.egide.dto.LoginRequestDTO;
import br.imd.ufrn.egide.dto.LoginResponseDTO;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Autenticação", description = "Autenticação e autorização")
// Controller responsável pela autenticação de usuários e consulta do perfil autenticado.
// Endpoints públicos (login) e protegidos (me) da rota /v1/auth.
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter Bearer token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password())
        );

        UserInfoEntity user = (UserInfoEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", jwtService.getExpirationInSeconds()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retornar dados do usuário autenticado")
    public ResponseEntity<AuthenticatedUserDTO> me(@AuthenticationPrincipal UserInfoEntity user) {
        AuthenticatedUserDTO response = new AuthenticatedUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
        return ResponseEntity.ok(response);
    }
}
