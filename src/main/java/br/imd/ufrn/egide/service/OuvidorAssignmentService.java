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

@Service
@RequiredArgsConstructor
public class OuvidorAssignmentService {

    static final int POOL_SIZE = 3;

    private static final List<ReportStatus> CLOSED_STATUSES = List.of(
            ReportStatus.CLOSED_NO_PROOFS,
            ReportStatus.REJECTED,
            ReportStatus.RESPONDED,
            ReportStatus.CLOSED
    );

    private final UserInfoRepository userInfoRepository;
    private final ReportRepository reportRepository;
    private final Random random = new SecureRandom();

    public UserInfoEntity assignOuvidor() {
        return assignOuvidor(List.of());
    }

    public UserInfoEntity assignOuvidor(List<Long> excludeOuvidorIds) {
        List<Long> excluded = excludeOuvidorIds == null ? List.of() : excludeOuvidorIds;

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
