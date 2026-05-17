package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ProofObservationRequestDTO;
import br.imd.ufrn.egide.dto.ProofObservationResponseDTO;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.PreliminaryReportEntity;
import br.imd.ufrn.egide.entity.ProofObservationEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.PreliminaryReportRepository;
import br.imd.ufrn.egide.repository.ProofObservationRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

// Fase 2 — permite ao Ouvidor registrar e consultar observações textuais sobre cada prova da manifestação.
@Service
@RequiredArgsConstructor
public class ProofObservationServiceImpl implements ProofObservationService {

    private final ProofObservationRepository proofObservationRepository;
    private final PreliminaryReportRepository preliminaryReportRepository;
    private final UserInfoRepository userInfoRepository;
    private final ReportService reportService;
    private final FileService fileService;

    // Cria ou atualiza a observação do Ouvidor para um arquivo específico; vincula ao parecer em andamento.
    @Override
    @Transactional
    public ProofObservationResponseDTO upsert(Long reportId, Long fileId, ProofObservationRequestDTO request) {
        if (request == null || request.observation() == null || request.observation().isBlank()) {
            throw new BusinessException("A observação é obrigatória.", HttpStatus.BAD_REQUEST);
        }

        ReportEntity report = reportService.findEntityById(reportId);
        FileEntity file = fileService.findById(fileId);

        if (file.getReport() == null || !Objects.equals(file.getReport().getId(), report.getId())) {
            throw new BusinessException(
                    "A prova informada não pertence à manifestação.",
                    HttpStatus.BAD_REQUEST
            );
        }

        UserInfoEntity ouvidor = requireOuvidor();
        ensureAssignedOuvidor(report, ouvidor);

        ProofObservationEntity entity = proofObservationRepository
                .findByReportAndFile(reportId, fileId)
                .orElseGet(ProofObservationEntity::new);

        entity.setFile(file);
        entity.setOuvidor(ouvidor);
        entity.setObservation(request.observation().trim());

        PreliminaryReportEntity preliminary = preliminaryReportRepository
                .findByReportId(reportId)
                .orElse(null);
        entity.setPreliminaryReport(preliminary);

        entity = proofObservationRepository.save(entity);
        return toDTO(entity);
    }

    // Lista todas as observações registradas pelo Ouvidor nos arquivos de uma manifestação.
    @Override
    public List<ProofObservationResponseDTO> listByReport(Long reportId) {
        reportService.findEntityById(reportId);
        return proofObservationRepository.findAllByReport(reportId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Retorna o usuário autenticado garantindo que é LISTENER ou ADMIN.
    private UserInfoEntity requireOuvidor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoEntity user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
        if (user.getRole() != Role.LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas Ouvidores podem registrar observações em provas.",
                    HttpStatus.FORBIDDEN
            );
        }
        return user;
    }

    // Impede que um ouvidor diferente do designado edite observações do caso.
    private void ensureAssignedOuvidor(ReportEntity report, UserInfoEntity ouvidor) {
        if (ouvidor.getRole() == Role.ADMIN) {
            return;
        }
        if (report.getOuvidor() == null || !Objects.equals(report.getOuvidor().getId(), ouvidor.getId())) {
            throw new BusinessException(
                    "Você não é o Ouvidor designado para este caso.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    // Converte a entidade de observação para o DTO de resposta.
    private ProofObservationResponseDTO toDTO(ProofObservationEntity entity) {
        FileEntity file = entity.getFile();
        return new ProofObservationResponseDTO(
                entity.getId(),
                file != null ? file.getId() : null,
                file != null ? file.getName() : null,
                entity.getOuvidor() != null ? entity.getOuvidor().getId() : null,
                entity.getObservation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
