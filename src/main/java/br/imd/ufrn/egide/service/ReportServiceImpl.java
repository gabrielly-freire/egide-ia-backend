package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.OuvidorCaseDTO;
import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.dto.SatisfactionSurveyRequestDTO;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.SatisfactionSurveyEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.event.ReportCreatedEvent;
import br.imd.ufrn.egide.mapper.ReportMapper;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.repository.SatisfactionSurveyRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
// Implementação de ReportService; gerencia criação, listagem e operações transversais das manifestações.
// Aplica regras de visibilidade para MANAGER (ocultação de dados com conflito de interesse)
// e lógica de sorteio de ouvidor via OuvidorAssignmentService.
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserInfoRepository userInfoRepository;
    private final SatisfactionSurveyRepository surveyRepository;
    private final OuvidorAssignmentService ouvidorAssignmentService;
    private final NotificationService notificationService;

    private static final String PROTOCOL_NUMBER_PREFIX = "PM";

    // Cria a manifestação, atribui ouvidor por sorteio, gera o número de protocolo (PM + id),
    // faz upload dos arquivos e publica o evento de IA após o commit da transação.
    @Override
    @Transactional
    public ReportDTO save(ReportRequestDTO reportRequestDTO, List<MultipartFile> files) {

        UserInfoEntity author = currentUser();

        ReportEntity entity = new ReportEntity();
        entity.setTitle(reportRequestDTO.title());
        entity.setDescription(reportRequestDTO.description());
        entity.setDateOfOccurrence(reportRequestDTO.dateOfOccurrence());
        entity.setUserInfo(author);
        entity.setStatus(ReportStatus.PENDING);

        entity.setOuvidor(ouvidorAssignmentService.assignOuvidor());
        entity = reportRepository.save(entity);
        entity.setProtocolNumber(PROTOCOL_NUMBER_PREFIX + entity.getId());
        entity = reportRepository.save(entity);

        if (entity.getOuvidor() != null) {
            notificationService.notifyOuvidorAssigned(entity.getId(), entity.getOuvidor().getId());
        }

        if (files != null && !files.isEmpty()) {
            fileService.upload(files, entity);
        }

        eventPublisher.publishEvent(new ReportCreatedEvent(entity.getId()));

        return reportMapper.toDTO(entity);
    }

    // Extrai o usuário autenticado do SecurityContextHolder; lança ResourceNotFoundException se não encontrado.
    private UserInfoEntity currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    // Retorna todas as manifestações ativas filtradas pela visibilidade do usuário autenticado.
    // Para MANAGER com conflito, substitui título e descrição pela versão anonimizada.
    public List<ReportDTO> findAll() {
        UserInfoEntity user = currentUser();
        return reportRepository.findAll()
                .stream()
                .filter(report -> isVisibleTo(report, user))
                .map(report -> toDTOForViewer(report, user))
                .collect(Collectors.toList());
    }

    // Retorna os casos atribuídos ao ouvidor autenticado; lança BusinessException 403 para outros papéis.
    @Override
    public List<OuvidorCaseDTO> findCasesAssignedToCurrentOuvidor() {
        UserInfoEntity user = currentUser();
        if (user.getRole() != Role.LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas Ouvidores podem listar casos atribuídos.",
                    HttpStatus.FORBIDDEN
            );
        }

        return reportRepository.findByOuvidorId(user.getId())
                .stream()
                .map(this::toOuvidorCaseDTO)
                .collect(Collectors.toList());
    }

    // Retorna entidades de manifestação pelos status informados; retorna lista vazia se statuses for nulo ou vazio.
    @Override
    public List<ReportEntity> findEntitiesByStatusIn(List<ReportStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return reportRepository.findByStatusIn(statuses);
    }

    // Verifica se a manifestação é visível para o usuário; MANAGER não vê manifestações
    // onde ele mesmo é listado como conflitado pela IA (managerConflict = true e seu id está em conflictedUserIds).
    private boolean isVisibleTo(ReportEntity report, UserInfoEntity viewer) {
        if (viewer.getRole() != Role.MANAGER) {
            return true;
        }
        ReportAiAnalysedEntity ai = report.getReportAiAnalysed();
        if (ai == null || !Boolean.TRUE.equals(ai.getManagerConflict())) {
            return true;
        }
        List<String> conflicted = ai.getConflictedUserIds();
        if (conflicted == null || conflicted.isEmpty()) {
            return true;
        }
        String currentId = String.valueOf(viewer.getId());
        return conflicted.stream().noneMatch(id -> Objects.equals(id, currentId));
    }

    // Converte a entidade para DTO adaptado ao papel do usuário; para MANAGER, substitui
    // título e descrição pelas versões anonimizadas quando disponíveis na análise de IA.
    private ReportDTO toDTOForViewer(ReportEntity report, UserInfoEntity viewer) {
        ReportDTO base = reportMapper.toDTO(report);
        if (viewer.getRole() != Role.MANAGER) {
            return base;
        }
        ReportAiAnalysedEntity ai = report.getReportAiAnalysed();
        if (ai == null) {
            return base;
        }
        String title = ai.getTitleAnonymized() != null ? ai.getTitleAnonymized() : base.title();
        String description = ai.getDescriptionAnonymized() != null ? ai.getDescriptionAnonymized() : base.description();
        return new ReportDTO(
                base.id(),
                base.protocolNumber(),
                title,
                description,
                base.dateOfOccurrence(),
                base.userInfoId(),
                base.status(),
                base.ouvidorId(),
                base.ouvidorName(),
                base.createdAt()
        );
    }

    // Converte a entidade de manifestação para o DTO de caso do ouvidor, incluindo categoria, risco
    // e indicador de parecer preliminar já emitido.
    private OuvidorCaseDTO toOuvidorCaseDTO(ReportEntity report) {
        ReportAiAnalysedEntity ai = report.getReportAiAnalysed();
        ReportCategory category = ai != null ? ai.getCategory() : null;
        ReportRisk risk = ai != null ? ai.getRisk() : null;
        boolean issued = report.getPreliminaryReport() != null;
        String status = report.getStatus() != null ? report.getStatus().name() : null;
        return new OuvidorCaseDTO(
                report.getId(),
                report.getProtocolNumber(),
                report.getTitle(),
                report.getDescription(),
                report.getDateOfOccurrence(),
                status,
                category,
                risk,
                issued,
                report.getCreatedAt()
        );
    }

    // Retorna as manifestações registradas pelo usuário autenticado, identificado pelo username do SecurityContext.
    public List<ReportDTO> findMyReports() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserInfoEntity user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return reportRepository.findByUserInfoId(user.getId())
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Retorna a manifestação pelo id como DTO; lança ResourceNotFoundException se não encontrada.
    public ReportDTO getById(Long id) {
        ReportEntity entity = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        return reportMapper.toDTO(entity);
    }

    // Retorna a entidade de manifestação pelo id; utilizado por outros services que precisam da entidade.
    public ReportEntity findEntityById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));
    }

    // Monta o mapa de estatísticas do dashboard: contadores por status e médias de satisfação das pesquisas.
    // Valores nulos das médias são substituídos por 0.0 para facilitar a exibição no front-end.
    @Override
    public Map<String, Object> getDashboardStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("total", reportRepository.count());
        status.put("pendentes", reportRepository.countByStatus(ReportStatus.PENDING));
        status.put("analisados", reportRepository.countByStatus(ReportStatus.ANALYZED));
        status.put("rejeitados", reportRepository.countByStatus(ReportStatus.REJECTED));

        Double avgSpeed = surveyRepository.getAverageSpeedRating();
        Double avgResolution = surveyRepository.getAverageResolutionRating();

        status.put("mediaAgilidade", avgSpeed != null ? avgSpeed : 0.0);
        status.put("mediaResolucao", avgResolution != null ? avgResolution : 0.0);
        return status;
    }

    // Persiste pesquisa de satisfação; verifica se a manifestação existe e impede respostas duplicadas.
    @Override
    @Transactional
    public void saveSurvey(Long reportId, SatisfactionSurveyRequestDTO dto) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        if (surveyRepository.existsByReportId(reportId)) {
            throw new BusinessException("Pesquisa já realizada para esta manifestação.", HttpStatus.BAD_REQUEST);
        }

        if (report.getStatus() != ReportStatus.CLOSED) {
            throw new BusinessException(
                    "A pesquisa só pode ser enviada após o relato ser marcado como concluído.",
                    org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }

        SatisfactionSurveyEntity survey = new SatisfactionSurveyEntity();
        survey.setReport(report);
        survey.setSpeedRating(dto.speedRating());
        survey.setResolutionRating(dto.resolutionRating());
        survey.setComments(dto.comments());

        surveyRepository.save(survey);
    }

    @Override
    @Transactional
    public ReportDTO concluirRelato(Long id) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relato não encontrado com o ID: " + id));

        if (report.getStatus() == ReportStatus.CLOSED) {
            throw new BusinessException("Este relato já está encerrado.", HttpStatus.BAD_REQUEST);
        }

        report.setStatus(ReportStatus.CLOSED);

        ReportEntity savedReport = reportRepository.save(report);

        if (savedReport.getOuvidor() != null) {
            notificationService.notifySlaExpired(savedReport.getId(), savedReport.getOuvidor().getId());
        }

        return reportMapper.toDTO(savedReport);
    }
}