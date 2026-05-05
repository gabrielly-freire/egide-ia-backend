package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportRespondRequestDTO;
import br.imd.ufrn.egide.dto.ReportRespondResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionRequestDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.ReportResponseEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.repository.ReportAiAnalysedRepository;
import br.imd.ufrn.egide.repository.ReportProcessedRepository;
import br.imd.ufrn.egide.repository.ReportResponseRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportResponseServiceImpl implements ReportResponseService {

    private final ReportService reportService;
    private final ReportAiService reportAiService;
    private final ReportAiAnalysedRepository reportAiAnalysedRepository;
    private final ReportProcessedRepository reportProcessedRepository;
    private final ReportResponseRepository reportResponseRepository;

    @Override
    public ReportResponseSuggestionResponseDTO suggestResponse(Long reportId) {

        ReportEntity report = reportService.findEntityById(reportId);
        ReportAiAnalysedEntity ai = reportAiAnalysedRepository.findByReportId(reportId).orElse(null);

        String title = ai != null && ai.getTitleAnonymized() != null ? ai.getTitleAnonymized() : report.getTitle();
        String description = ai != null && ai.getDescriptionAnonymized() != null ? ai.getDescriptionAnonymized() : report.getDescription();

        String category = ai != null && ai.getCategory() != null ? ai.getCategory().name() : null;
        String risk = ai != null && ai.getRisk() != null ? ai.getRisk().name() : null;

        return reportAiService.suggestResponse(
                new ReportResponseSuggestionRequestDTO(
                        report.getId(),
                        title,
                        description,
                        report.getProtocolNumber(),
                        category,
                        risk
                )
        );
    }

    @Override
    @Transactional
    public ReportRespondResponseDTO respond(Long reportId, ReportRespondRequestDTO request) {

        String provided = request != null ? request.responseText() : null;
        String aiSuggestion = request != null ? request.aiSuggestion() : null;

        boolean usedAiSuggestion;
        String finalResponseText;

        if (isBlank(provided)) {
            if (isBlank(aiSuggestion)) {
                throw new BusinessException("Informe responseText ou aiSuggestion para responder a manifestação", HttpStatus.BAD_REQUEST);
            }
            usedAiSuggestion = true;
            finalResponseText = aiSuggestion;
        } else {
            finalResponseText = provided;
            usedAiSuggestion = !isBlank(aiSuggestion) && normalize(finalResponseText).equals(normalize(aiSuggestion));
        }

        ReportEntity report = reportService.findEntityById(reportId);
        report.setStatus(ReportStatus.RESPONDED);

        ReportResponseEntity responseEntity = reportResponseRepository.findByReportId(reportId)
                .orElseGet(ReportResponseEntity::new);
        responseEntity.setReport(report);
        responseEntity.setResponseText(finalResponseText);
        responseEntity.setAiSuggestion(aiSuggestion);
        responseEntity.setUsedAiSuggestion(usedAiSuggestion);
        responseEntity.setRespondedAt(LocalDateTime.now());
        reportResponseRepository.save(responseEntity);

        ReportProcessedEntity processed = reportProcessedRepository.findByReportId(reportId).orElse(null);
        if (processed != null) {
            processed.setStatus(ReportStatus.RESPONDED);
            reportProcessedRepository.save(processed);
        }

        String status = report.getStatus() != null ? report.getStatus().name() : ReportStatus.RESPONDED.name();

        return new ReportRespondResponseDTO(
                reportId,
                finalResponseText,
                aiSuggestion,
                usedAiSuggestion,
                status,
                responseEntity.getRespondedAt()
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    @Override
    public ReportRespondResponseDTO getResponse(Long reportId) {
        ReportResponseEntity responseEntity = reportResponseRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Resposta não encontrada"));

        ReportEntity report = reportService.findEntityById(reportId);
        String status = report.getStatus() != null ? report.getStatus().name() : ReportStatus.RESPONDED.name();

        return new ReportRespondResponseDTO(
                reportId,
                responseEntity.getResponseText(),
                responseEntity.getAiSuggestion(),
                responseEntity.getUsedAiSuggestion(),
                status,
                responseEntity.getRespondedAt()
        );
    }
}
