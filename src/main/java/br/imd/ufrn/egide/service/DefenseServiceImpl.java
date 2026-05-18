package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.DefenseDTO;
import br.imd.ufrn.egide.dto.DefenseRequestDTO;
import br.imd.ufrn.egide.dto.FileDTO;
import br.imd.ufrn.egide.entity.DefenseEntity;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.repository.DefenseRepository;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefenseServiceImpl implements DefenseService {

    private final ReportRepository reportRepository;
    private final DefenseRepository defenseRepository;
    private final UserInfoRepository userInfoRepository;
    private final FileService fileService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public DefenseDTO submitDefense(Long reportId, DefenseRequestDTO request, List<MultipartFile> files) {

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        if (report.getStatus() == ReportStatus.REJECTED
                || report.getStatus() == ReportStatus.CLOSED_NO_PROOFS
                || report.getStatus() == ReportStatus.CLOSED) {
            throw new BusinessException("Denúncia encerrada. Não é possível enviar defesa.", HttpStatus.BAD_REQUEST);
        }

        if (report.getStatus() != ReportStatus.DEFENSE_OPEN) {
            throw new BusinessException("Defesa só pode ser enviada quando o caso estiver em defesa.", HttpStatus.BAD_REQUEST);
        }

        UserInfoEntity currentUser = getCurrentUser();
        if (report.getDenouncedUser() == null || !report.getDenouncedUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("Apenas o denunciado pode enviar a defesa deste caso.", HttpStatus.FORBIDDEN);
        }

        DefenseEntity existing = defenseRepository.findByReportId(reportId).orElse(null);
        if (existing != null && existing.getSubmittedAt() != null) {
            throw new BusinessException("Defesa já enviada para este caso.", HttpStatus.CONFLICT);
        }

        DefenseEntity defense = existing != null ? existing : new DefenseEntity();
        defense.setReport(report);
        defense.setSubmittedBy(currentUser);
        defense.setDefenseText(request.defenseText());
        defense.setSubmittedAt(LocalDateTime.now());
        defense = defenseRepository.save(defense);

        if (files != null && !files.isEmpty()) {
            fileService.uploadForDefense(files, defense);
        }

        report.setStatus(ReportStatus.DEFENSE_UNDER_ANALYSIS);
        reportRepository.save(report);

        if (report.getOuvidor() != null) {
            notificationService.notifyOuvidorDefenseSubmitted(report.getId(), report.getOuvidor().getId());
        }

        DefenseEntity refreshed = defenseRepository.findById(defense.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Defesa não encontrada"));

        return toDTO(refreshed);
    }

    @Override
    public DefenseDTO getDefense(Long reportId) {
        DefenseEntity defense = defenseRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Defesa não encontrada"));

        UserInfoEntity currentUser = getCurrentUser();
        boolean canView = isPrivileged() || defense.getSubmittedBy().getId().equals(currentUser.getId());
        if (!canView) {
            throw new BusinessException("Sem permissão para visualizar esta defesa.", HttpStatus.FORBIDDEN);
        }

        return toDTO(defense);
    }

    private DefenseDTO toDTO(DefenseEntity defense) {
        List<FileDTO> files = defense.getFiles() == null
                ? List.of()
                : defense.getFiles().stream().map(this::toFileDTO).toList();

        return new DefenseDTO(
                defense.getReport().getId(),
                defense.getDefenseText(),
                defense.getSubmittedAt(),
                defense.getSubmittedBy().getId(),
                files
        );
    }

    private FileDTO toFileDTO(FileEntity file) {
        return new FileDTO(file.getId(), file.getName(), file.getContentType(), file.getSize());
    }

    private UserInfoEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private boolean isPrivileged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_LISTENER".equals(a.getAuthority())
                        || "ROLE_MANAGER".equals(a.getAuthority())
                        || "ROLE_ADMIN".equals(a.getAuthority())
        );
    }
}
