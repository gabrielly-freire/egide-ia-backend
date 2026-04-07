package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.Role;

public record AuthenticatedUserDTO(
        Long id,
        String name,
        String email,
        String username,
        Role role
) {
}
