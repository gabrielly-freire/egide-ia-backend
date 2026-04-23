package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.event.ReportCreatedEvent;
import br.imd.ufrn.egide.mapper.ReportMapper;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserInfoService userInfoService;

    private static final String PROTOCOL_NUMBER_PREFIX = "PM";

    @Override
    @Transactional
    public ReportDTO save(ReportRequestDTO reportRequestDTO, List<MultipartFile> files) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserInfoEntity user)) {
//            throw new BusinessException("Usuário inválido", HttpStatus.UNAUTHORIZED);
//        }

        UserInfoEntity user = userInfoService.findById(1L);

        ReportEntity entity = new ReportEntity();
        entity.setTitle(reportRequestDTO.title());
        entity.setDescription(reportRequestDTO.description());
        entity.setDateOfOccurrence(reportRequestDTO.dateOfOccurrence());
        entity.setUserInfo(user);
        entity.setStatus(ReportStatus.PENDING);
        entity = reportRepository.save(entity);
        entity.setProtocolNumber(PROTOCOL_NUMBER_PREFIX + entity.getId());
        entity = reportRepository.save(entity);

        if (files != null && !files.isEmpty()) {
            fileService.upload(files, entity);
        }

        eventPublisher.publishEvent(new ReportCreatedEvent(entity.getId()));

        return reportMapper.toDTO(entity);
    }

    public List<ReportDTO> findAll() {
        return reportRepository.findAll()
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
    public Map<String, Long> getDashboardStatus() {
        Map<String, Long> status = new HashMap<>();
        status.put("total", reportRepository.count());
        status.put("pendentes", reportRepository.countByStatus(ReportStatus.PENDING));
        status.put("analisados", reportRepository.countByStatus(ReportStatus.ANALYZED));
        status.put("rejeitados", reportRepository.countByStatus(ReportStatus.REJECTED));
        return status;
    }
}