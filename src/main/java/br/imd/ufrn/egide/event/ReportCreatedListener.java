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
public class ReportCreatedListener {

    private final ReportAiService reportAiService;

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
