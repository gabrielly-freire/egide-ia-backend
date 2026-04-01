package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.mapper.ReportMapper;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReportServiceImpl {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportDTO save(ReportDTO reportDTO) {
        ReportEntity entity = reportMapper.toEntity(reportDTO);

        entity.setStatus(ReportStatus.PENDING);
        entity = reportRepository.save(entity);

        return reportMapper.toDTO(entity);
    }

    public ReportEntity getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));
    }
}