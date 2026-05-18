package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.NotificationResponseDTO;
import br.imd.ufrn.egide.entity.NotificationEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.NotificationType;
import br.imd.ufrn.egide.repository.NotificationRepository;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserInfoRepository userInfoRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void notifyDenouncedPhase3Started(Long reportId, Long denouncedUserId) {
        if (reportId == null || denouncedUserId == null) {
            throw new BusinessException("Parâmetros inválidos para notificação.", HttpStatus.BAD_REQUEST);
        }

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));

        UserInfoEntity denounced = userInfoRepository.findById(denouncedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário denunciado não encontrado"));

        String protocol = report.getProtocolNumber() != null ? report.getProtocolNumber() : String.valueOf(report.getId());

        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(denounced);
        notification.setReport(report);
        notification.setType(NotificationType.PHASE3_STARTED);
        notification.setTitle("Defesa aberta");
        notification.setMessage("Você foi indicado como denunciado na manifestação " + protocol + ". Envie sua defesa.");

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> listMyNotifications() {
        UserInfoEntity user = currentUser();
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UserInfoEntity user = currentUser();
        return notificationRepository.countByRecipientIdAndReadAtIsNull(user.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        if (notificationId == null) {
            throw new BusinessException("Notificação inválida.", HttpStatus.BAD_REQUEST);
        }

        UserInfoEntity user = currentUser();
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (notification.getRecipient() == null || !Objects.equals(notification.getRecipient().getId(), user.getId())) {
            throw new BusinessException("Você não tem permissão para acessar esta notificação.", HttpStatus.FORBIDDEN);
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        UserInfoEntity user = currentUser();
        notificationRepository.markAllAsRead(user.getId(), LocalDateTime.now());
    }

    private NotificationResponseDTO toDTO(NotificationEntity entity) {
        return new NotificationResponseDTO(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getCreatedAt(),
                entity.getReadAt(),
                entity.getReport() != null ? entity.getReport().getId() : null
        );
    }

    private UserInfoEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    @Override
    @Transactional
    public void notifySlaExpired(Long reportId, Long recipientId) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));


        UserInfoEntity recipient = userInfoRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));


        String protocol = report.getProtocolNumber() != null ? report.getProtocolNumber() : String.valueOf(report.getId());


        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(recipient);
        notification.setReport(report);
        notification.setType(NotificationType.SLA_EXPIRED); // era PHASE3_STARTED — tipo incorreto
        notification.setTitle("Prazo Expirado");
        notification.setMessage("A manifestação " + protocol + " excedeu o prazo de 10 dias.");

        notificationRepository.save(notification);
    }

    // Notifica o ouvidor sorteado logo após a criação da manifestação.
    @Override
    @Transactional
    public void notifyOuvidorAssigned(Long reportId, Long ouvidorId) {
        if (reportId == null || ouvidorId == null) {
            throw new BusinessException("Parâmetros inválidos para notificação.", HttpStatus.BAD_REQUEST);
        }

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));

        UserInfoEntity ouvidor = userInfoRepository.findById(ouvidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvidor não encontrado"));

        String protocol = report.getProtocolNumber() != null ? report.getProtocolNumber() : String.valueOf(report.getId());

        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(ouvidor);
        notification.setReport(report);
        notification.setType(NotificationType.OUVIDOR_ASSIGNED);
        notification.setTitle("Novo caso atribuído");
        notification.setMessage("A manifestação " + protocol + " foi atribuída a você para análise.");

        notificationRepository.save(notification);
    }

    // Notifica o denunciante quando o parecer preliminar é emitido.
    @Override
    @Transactional
    public void notifyDenunciantePreliminaryIssued(Long reportId, Long denuncianteId) {
        if (reportId == null || denuncianteId == null) {
            throw new BusinessException("Parâmetros inválidos para notificação.", HttpStatus.BAD_REQUEST);
        }

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));

        UserInfoEntity denunciante = userInfoRepository.findById(denuncianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Denunciante não encontrado"));

        String protocol = report.getProtocolNumber() != null ? report.getProtocolNumber() : String.valueOf(report.getId());

        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(denunciante);
        notification.setReport(report);
        notification.setType(NotificationType.PRELIMINARY_REPORT_ISSUED);
        notification.setTitle("Parecer emitido");
        notification.setMessage("O parecer preliminar da manifestação " + protocol + " foi emitido. Acesse o sistema para acompanhar.");

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyOuvidorDefenseSubmitted(Long reportId, Long ouvidorId) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));

        UserInfoEntity ouvidor = userInfoRepository.findById(ouvidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvidor não encontrado"));

        String protocol = report.getProtocolNumber() != null
                ? report.getProtocolNumber()
                : String.valueOf(report.getId());

        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(ouvidor);
        notification.setReport(report);
        notification.setType(NotificationType.DEFENSE_SUBMITTED);
        notification.setTitle("Defesa recebida");
        notification.setMessage("O denunciado enviou a defesa da manifestação " + protocol + ". Analise para emitir o relatório final.");

        notificationRepository.save(notification);
    }

}
