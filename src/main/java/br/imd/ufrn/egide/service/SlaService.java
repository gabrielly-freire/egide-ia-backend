package br.imd.ufrn.egide.service;


import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SlaService {


    private final ReportRepository repository;
    private final NotificationService notificationService;


    private static final int SLA_DAYS = 10;


    @Scheduled(cron = "0 0 0 * * ?") // Executa todo dia à meia-noite
    public void checkAllDeadlines() {
        log.info("Iniciando verificação diária de SLA...");


        // Status que exigem atenção do ouvidor
        List<ReportStatus> activeStatuses = List.of(
                ReportStatus.PENDING,
                ReportStatus.DEFENSE_OPEN,
                ReportStatus.APPEAL_UNDER_ANALYSIS
        );


        List<ReportEntity> reports = repository.findByStatusIn(activeStatuses);
        LocalDateTime limitDate = LocalDateTime.now().minusDays(SLA_DAYS);


        for (ReportEntity report : reports) {
            if (report.getUpdatedAt() != null && report.getUpdatedAt().isBefore(limitDate)) {
                if (report.getOuvidor() != null) {
                    log.warn("SLA violado para a manifestação {}. Notificando ouvidor {}",
                            report.getProtocolNumber(), report.getOuvidor().getName());
                    notificationService.notifySlaExpired(report.getId(), report.getOuvidor().getId());
                }
            }
        }
    }


    private void notifyResponsible(ReportEntity report) {
        if (report.getOuvidor() != null) {
            log.warn("SLA violado para a manifestação {}. Notificando ouvidor {}",
                    report.getProtocolNumber(), report.getOuvidor().getName());


            notificationService.notifyDenouncedPhase3Started(report.getId(), report.getOuvidor().getId());
        }
    }
}