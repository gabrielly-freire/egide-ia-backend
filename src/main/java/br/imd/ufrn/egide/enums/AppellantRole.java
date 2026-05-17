package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Identifica o papel da parte que abriu o recurso na Fase 5.
// Junto com report_id, forma a chave de unicidade que impede uma mesma parte
// de submeter mais de um recurso para o mesmo caso.
// O papel é inferido automaticamente pelo AppealServiceImpl com base no vínculo do
// usuário autenticado com a manifestação (userInfo = DENUNCIANTE, denunciadoUser = DENUNCIADO);
// o campo appellantRole no DTO é apenas um hint usado quando o papel não pode ser inferido
// (ex.: ADMIN abrindo recurso em nome de uma parte).
@AllArgsConstructor
@Getter
public enum AppellantRole {
    DENUNCIANTE("Denunciante"),
    DENUNCIADO("Denunciado");

    private final String description;
}
