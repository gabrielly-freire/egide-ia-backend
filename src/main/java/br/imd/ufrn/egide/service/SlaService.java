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
// Serviço de monitoramento de SLA das manifestações pendentes.
// Executa verificações periódicas a cada 60 segundos para alertar sobre atrasos e prazos iminentes.
// O prazo padrão é de 5 dias; manifestações PENDING além desse prazo geram alertas de violação.
public class SlaService {

    @Autowired
    private ReportRepository repository;

    private final int prazo = 5;

    // Verifica se há manifestações PENDING com mais de 5 dias sem atualização e registra alerta de SLA.
    @Scheduled(fixedRate = 60000)
    public void checkSla() {
        LocalDateTime limite = LocalDateTime.now().minusDays(prazo);

        var atrasados = repository.findAllByStatusAndCreatedAtBefore(ReportStatus.PENDING, limite);

        if (!atrasados.isEmpty()) {
            log.warn("⚠️ ALERTA DE SLA: Existem {} manifestações pendentes há mais de {} dias!",
                    atrasados.size(), prazo);
        }
    }

    // Verifica manifestações PENDING que vencerão em menos de 24 horas e registra alerta preventivo.
    // A janela de 10 minutos evita alertas duplicados a cada execução do scheduler.
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