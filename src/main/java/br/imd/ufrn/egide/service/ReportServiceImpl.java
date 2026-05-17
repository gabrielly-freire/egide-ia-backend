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
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserInfoRepository userInfoRepository;
    private final SatisfactionSurveyRepository surveyRepository;
    private final OuvidorAssignmentService ouvidorAssignmentService;

    private static final String PROTOCOL_NUMBER_PREFIX = "PM";

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

        if (files != null && !files.isEmpty()) {
            fileService.upload(files, entity);
        }

        eventPublisher.publishEvent(new ReportCreatedEvent(entity.getId()));

        return reportMapper.toDTO(entity);
    }

    private UserInfoEntity currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    public List<ReportDTO> findAll() {
        UserInfoEntity user = currentUser();
        return reportRepository.findAll()
                .stream()
                .filter(report -> isVisibleTo(report, user))
                .map(report -> toDTOForViewer(report, user))
                .collect(Collectors.toList());
    }

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

    @Override
    public List<ReportEntity> findEntitiesByStatusIn(List<ReportStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return reportRepository.findByStatusIn(statuses);
    }

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
                base.ouvidorName()
        );
    }

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

    public List<ReportDTO> findMyReports() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserInfoEntity user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return reportRepository.findByUserInfoId(user.getId())
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReportDTO getById(Long id) {
        ReportEntity entity = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        return reportMapper.toDTO(entity);
    }

    public ReportEntity findEntityById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));
    }

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

    @Override
    @Transactional
    public void saveSurvey(Long reportId, SatisfactionSurveyRequestDTO dto) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        if (surveyRepository.existsByReportId(reportId)) {
            throw new BusinessException("Pesquisa já realizada para esta manifestação.", HttpStatus.BAD_REQUEST);
        }

        SatisfactionSurveyEntity survey = new SatisfactionSurveyEntity();
        survey.setReport(report);
        survey.setSpeedRating(dto.speedRating());
        survey.setResolutionRating(dto.resolutionRating());
        survey.setComments(dto.comments());

        surveyRepository.save(survey);
    }
}