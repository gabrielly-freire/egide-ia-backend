package br.imd.ufrn.egide.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import br.imd.ufrn.egide.dto.ReportAiFileProcessing;
import br.imd.ufrn.egide.dto.ReportAnalysedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnalysedResponseDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionRequestDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponsibleUserDTO;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.ReportAiAnalysedRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// Implementação de ReportAiService; coordena chamadas ao microsserviço de IA via RestClient.
// Todas as operações de IO com o serviço externo são síncronas neste service; a execução assíncrona
// é responsabilidade do ReportCreatedListener (pool aiExecutor).
public class ReportAiServiceImpl implements ReportAiService {

    private final RestClient restClient;
    private final ReportService reportService;
    private final ReportAiAnalysedRepository reportAiAnalysedRepository;
    private final UserInfoRepository userInfoRepository;

    // Orquestra o pipeline completo: busca a manifestação, anonimiza, converte arquivos para base64,
    // classifica via IA e persiste o resultado em ReportAiAnalysedEntity (upsert por reportId).
    @Override
    @Transactional
    public void processReport(Long reportId) {
        ReportEntity report = reportService.findEntityById(reportId);

        ReportAnonymizedResponseDTO anonymizeResponse = anonymize(
                new ReportAnonymizedRequestDTO(report.getId(), report.getTitle(), report.getDescription())
        );

        List<ReportAiFileProcessing> files = toAiFiles(report.getFiles());

        List<ReportResponsibleUserDTO> responsibleUsers = userInfoRepository
                .findAllByRoleIn(List.of(Role.MANAGER, Role.LISTENER))
                .stream()
                .map(u -> new ReportResponsibleUserDTO(
                        String.valueOf(u.getId()),
                        u.getName(),
                        u.getEmail(),
                        u.getUsername(),
                        u.getRole().name()
                ))
                .toList();

        ReportAnalysedResponseDTO analysisResponse = classify(
                new ReportAnalysedRequestDTO(report.getId(), report.getTitle(), report.getDescription(), files, responsibleUsers)
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

    // Chama o endpoint /compliance/anonimizar do microsserviço de IA e retorna a resposta deserializada.
    @Override
    public ReportAnonymizedResponseDTO anonymize(ReportAnonymizedRequestDTO request) {
        return restClient.post()
                .uri("/compliance/anonimizar")
                .body(request)
                .retrieve()
                .body(ReportAnonymizedResponseDTO.class);
    }
    // Chama o endpoint /analysis/analisar do microsserviço de IA e retorna a classificação com detecção de conflito.
    @Override
    public ReportAnalysedResponseDTO classify(ReportAnalysedRequestDTO request) {
        return restClient.post()
                .uri("/analysis/analisar")
                .body(request)
                .retrieve()
                .body(ReportAnalysedResponseDTO.class);
    }

    // Chama o endpoint /compliance/sugerir-resposta do microsserviço de IA e retorna a sugestão textual.
    @Override
    public ReportResponseSuggestionResponseDTO suggestResponse(ReportResponseSuggestionRequestDTO request) {
        return restClient.post()
                .uri("/compliance/sugerir-resposta")
                .body(request)
                .retrieve()
                .body(ReportResponseSuggestionResponseDTO.class);
    }

    // Converte a lista de entidades de arquivo para DTOs com conteúdo base64; retorna lista vazia se nula ou vazia.
    private List<ReportAiFileProcessing> toAiFiles(List<FileEntity> fileEntities) {

        if (fileEntities == null || fileEntities.isEmpty()) {
            return List.of();
        }

        return fileEntities.stream()
                .map(this::toAiFile)
                .toList();
    }

    // Lê o conteúdo binário do arquivo do disco e codifica em base64 para envio ao serviço de IA.
    // Lança BusinessException com 500 se o arquivo não puder ser lido.
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
