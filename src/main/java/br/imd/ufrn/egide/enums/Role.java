package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Papéis dos atores do sistema de Ouvidoria; usados como autoridade de acesso nos @PreAuthorize.
// A hierarquia de permissões é: ADMIN > GENERAL_LISTENER > MANAGER > LISTENER > REMONSTRANT.
// Regras de negócio por papel:
//   - REMONSTRANT: abre manifestações e pode submeter recurso na Fase 5.
//   - LISTENER: ouvidor designado por sorteio; responsável pelos pareceres das Fases 2, 3 e (quando novo ouvidor) 5.
//   - GENERAL_LISTENER: Ouvidor Geral; valida, altera ou repassa relatórios na Fase 4.
//   - MANAGER: papel de gestão institucional; permissões específicas definidas fora deste escopo.
//   - ADMIN: acesso irrestrito a todos os endpoints; útil para suporte e operações de manutenção.
@AllArgsConstructor
@Getter
public enum Role {
    REMONSTRANT("Reclamante"),
    LISTENER("Ouvidor"),
    GENERAL_LISTENER("Ouvidor Geral"),
    MANAGER("Gestor"),
    ADMIN("Administrador");

    private String description;
}
