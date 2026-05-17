package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.Role;

// Representa os dados do usuário autenticado retornados pelo endpoint GET /v1/auth/me.
// Expõe apenas os campos necessários para identificação e controle de acesso no front-end.
public record AuthenticatedUserDTO(
        Long id,
        String name,
        String email,
        String username,
        Role role
) {
}
