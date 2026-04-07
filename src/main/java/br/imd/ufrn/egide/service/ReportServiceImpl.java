package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.mapper.ReportMapper;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public ReportDTO save(ReportDTO reportDTO) {
        ReportEntity entity = reportMapper.toEntity(reportDTO);

        entity.setStatus(ReportStatus.PENDING);
        entity = reportRepository.save(entity);

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

    @Override
    public ReportDTO updateCategory(Long id, ReportCategory category) {
        ReportEntity entity = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));
        entity.setCategory(category);

        entity = reportRepository.save(entity);
        return reportMapper.toDTO(entity);
    }
}