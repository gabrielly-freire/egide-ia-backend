package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Serviço responsável pela designação de ouvidores via sorteio justo.
// Implementa o algoritmo de balanceamento de carga: ordena todos os ouvidores elegíveis
// pelo número de casos ativos em ordem crescente, forma uma pool com os 3 de menor carga
// e sorteia aleatoriamente um deles usando SecureRandom (criptograficamente seguro).
// Suporta exclusão de IDs para garantir anti-viés nos seguintes cenários:
//   - Repass pela OG: exclui todos os ouvidores que já participaram do caso.
//   - Recurso (Fase 5): exclui ouvidor original, ouvidor do parecer e do relatório final.
// A lista CLOSED_STATUSES define quais estados NÃO contam como carga ativa,
// evitando que casos encerrados inflem artificialmente a carga de um ouvidor.
@Service
@RequiredArgsConstructor
// Designa ouvidores via sorteio na pool dos 3 com menor carga ativa; suporta exclusão de IDs para anti-viés em repass e recurso.
public class OuvidorAssignmentService {

    // Tamanho da pool de candidatos; os 3 ouvidores com menor carga ativa concorrem ao sorteio.
    static final int POOL_SIZE = 3;

    // Estados que representam casos encerrados e não contabilizam na carga ativa do ouvidor.
    private static final List<ReportStatus> CLOSED_STATUSES = List.of(
            ReportStatus.CLOSED_NO_PROOFS,
            ReportStatus.REJECTED,
            ReportStatus.RESPONDED,
            ReportStatus.CLOSED
    );

    private final UserInfoRepository userInfoRepository;
    private final ReportRepository reportRepository;
    // SecureRandom usado em vez de Random padrão para evitar previsibilidade no sorteio.
    private final Random random = new SecureRandom();

    // Sorteia ouvidor sem exclusões — usado na designação inicial ao criar uma manifestação.
    public UserInfoEntity assignOuvidor() {
        return assignOuvidor(List.of());
    }

    // Sorteia ouvidor excluindo IDs informados; lança exceção se não restar candidato elegível.
    public UserInfoEntity assignOuvidor(List<Long> excludeOuvidorIds) {
        List<Long> excluded = excludeOuvidorIds == null ? List.of() : excludeOuvidorIds;

        // Busca apenas usuários com papel LISTENER; exclui os IDs informados (anti-viés).
        List<UserInfoEntity> ouvidores = userInfoRepository.findAllByRoleIn(List.of(Role.LISTENER))
                .stream()
                .filter(o -> !excluded.contains(o.getId()))
                .toList();

        if (ouvidores.isEmpty()) {
            throw new BusinessException(
                    "Não há Ouvidores elegíveis para designação (todos foram excluídos ou nenhum está cadastrado).",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        // Ordena por carga ativa (casos não encerrados), usa ID como critério de desempate para
        // garantir ordem determinística entre execuções e, portanto, fairness no sorteio.
        List<UserInfoEntity> pool = ouvidores.stream()
                .sorted(Comparator
                        .comparingLong((UserInfoEntity o) -> reportRepository
                                .countActiveCasesForOuvidor(o.getId(), CLOSED_STATUSES))
                        .thenComparing(UserInfoEntity::getId))
                .limit(POOL_SIZE)
                .toList();

        return pool.get(random.nextInt(pool.size()));
    }
}
