package br.imd.ufrn.egide.event;

import br.imd.ufrn.egide.service.ReportAiService;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
// Listener de eventos de criação de manifestação; aciona o pipeline de análise de IA de forma assíncrona.
// Usa @TransactionalEventListener com fase AFTER_COMMIT para garantir que a manifestação esteja
// persistida antes de iniciar o processamento de IA, evitando leitura de dados não comitados.
public class ReportCreatedListener {

    private final ReportAiService reportAiService;

    // Processa a manifestação recém-criada pelo serviço de IA após o commit da transação.
    // Executado no pool "aiExecutor" para não bloquear a thread da requisição HTTP.
    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReportCreated(ReportCreatedEvent event) {
        try {
            reportAiService.processReport(event.reportId());
        } catch (Exception ex) {
            throw new BusinessException("Erro ao processar IA para reportId={}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
