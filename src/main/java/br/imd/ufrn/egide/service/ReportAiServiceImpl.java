package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.*;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.repository.ReportAiAnalysedRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAiServiceImpl implements ReportAiService {

    private final RestClient restClient;
    private final ReportService reportService;
    private final ReportAiAnalysedRepository reportAiAnalysedRepository;


    @Override
    @Transactional
    public void processReport(Long reportId) {
        ReportEntity report = reportService.findEntityById(reportId);

        ReportAnonymizedResponseDTO anonymizeResponse = anonymize(
                new ReportAnonymizedRequestDTO(report.getId(), report.getTitle(), report.getDescription())
        );

        List<ReportAiFileProcessing> files = toAiFiles(report.getFiles());

        ReportAnalysedResponseDTO analysisResponse = classify(
                new ReportAnalysedRequestDTO(report.getId(), report.getTitle(), report.getDescription(), files)
        );

        ReportAiAnalysedEntity entity = reportAiAnalysedRepository.findByReportId(report.getId())
                .orElseGet(ReportAiAnalysedEntity::new);
        entity.setReport(report);
        entity.setTitleAnonymized(anonymizeResponse.anonymizedTitle());
        entity.setDescriptionAnonymized(anonymizeResponse.anonymizedDescription());
        entity.setCategory(analysisResponse.category());
        entity.setRisk(analysisResponse.risk());
        entity.setConflictDetected(analysisResponse.conflictDetected());
        entity.setConflictedUserIds(analysisResponse.conflictedUserIds());
        entity.setManagerConflict(analysisResponse.managerConflict());
        reportAiAnalysedRepository.save(entity);
    }

    @Override
    public ReportAnonymizedResponseDTO anonymize(ReportAnonymizedRequestDTO request) {
        return restClient.post()
                .uri("/compliance/anonimizar")
                .body(request)
                .retrieve()
                .body(ReportAnonymizedResponseDTO.class);
    }
    @Override
    public ReportAnalysedResponseDTO classify(ReportAnalysedRequestDTO request) {
        return restClient.post()
                .uri("/analysis/analisar")
                .body(request)
                .retrieve()
                .body(ReportAnalysedResponseDTO.class);
    }

    @Override
    public ReportResponseSuggestionResponseDTO suggestResponse(ReportResponseSuggestionRequestDTO request) {
        return restClient.post()
                .uri("/compliance/sugerir-resposta")
                .body(request)
                .retrieve()
                .body(ReportResponseSuggestionResponseDTO.class);
    }

    private List<ReportAiFileProcessing> toAiFiles(List<FileEntity> fileEntities) {

        if (fileEntities == null || fileEntities.isEmpty()) {
            return List.of();
        }

        return fileEntities.stream()
                .map(this::toAiFile)
                .toList();
    }

    private ReportAiFileProcessing toAiFile(FileEntity file) {

        try {

            byte[] content = Files.readAllBytes(Path.of(file.getPath()));
            String base64 = Base64.getEncoder().encodeToString(content);

            return new ReportAiFileProcessing(
                    file.getName(),
                    file.getContentType(),
                    base64
            );

        } catch (IOException ex) {

            throw new BusinessException(
                    "Não foi possível ler o arquivo para análise de IA: " + file.getName(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
