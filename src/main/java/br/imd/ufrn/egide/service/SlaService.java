package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SlaService {

    @Autowired
    private ReportRepository repository;

    private final int prazo = 5;

    @Scheduled(fixedRate = 60000)
    public void checkSla() {
        LocalDateTime limite = LocalDateTime.now().minusDays(prazo);

        var atrasados = repository.findAllByStatusAndCreatedAtBefore(ReportStatus.PENDING, limite);

        if (!atrasados.isEmpty()) {
            log.warn("⚠️ ALERTA DE SLA: Existem {} manifestações pendentes há mais de {} dias!",
                    atrasados.size(), prazo);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkPreventiveSla() {
        int diasPreventivo = prazo - 1;

        LocalDateTime inicioJanela = LocalDateTime.now().minusDays(diasPreventivo).minusMinutes(10);
        LocalDateTime fimJanela = LocalDateTime.now().minusDays(diasPreventivo);

        var quaseAtrasados = repository.findAllByStatusAndCreatedAtBetween(
                ReportStatus.PENDING, inicioJanela, fimJanela
        );

        if (!quaseAtrasados.isEmpty()) {
            log.info("🔔 ALERTA PREVENTIVO: {} manifestações vencerão em 24 horas!", quaseAtrasados.size());
        }
    }
}