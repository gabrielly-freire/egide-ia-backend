package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada/saída para gerenciamento de usuários do sistema.
// O campo password é exigido na criação/atualização, mas não deve ser retornado em leituras
// (o mapper deve excluí-lo; caso contrário, o hash BCrypt será exposto).
public record UserInfoDTO(
        Long id,
        
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,
        
        @NotBlank(message = "O nome é obrigatório")
        String name,
        
        @NotBlank(message = "O nome de usuário é obrigatório")
        @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres")
        String username,
        
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @NotNull(message = "O papel (role) é obrigatório")
        Role role,

        @NotNull(message = "O departamento é obrigatório")
        Long departmentId
) {
}
