package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportProcessedDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import br.imd.ufrn.egide.mapper.ReportProcessedMapper;
import br.imd.ufrn.egide.repository.ReportProcessedRepository;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReportProcessedServiceImpl implements ReportProcessedService {

    private final ReportProcessedRepository reportProcessedRepository;
    private final ReportRepository reportRepository;
    private final ReportProcessedMapper reportProcessedMapper;

    @Override
    public ReportProcessedDTO save(ReportProcessedDTO dto) {
        ReportEntity report = reportRepository.findById(dto.reportId())
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        ReportProcessedEntity entity = reportProcessedMapper.toEntity(dto);
        entity.setReport(report);
        entity = reportProcessedRepository.save(entity);
        return reportProcessedMapper.toDTO(entity);
    }

    @Override
    public ReportProcessedDTO update(Long id, ReportProcessedDTO dto) {
        reportProcessedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        ReportEntity report = reportRepository.findById(dto.reportId())
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        ReportProcessedEntity entity = reportProcessedMapper.toEntity(dto);
        entity.setId(id);
        entity.setReport(report);
        entity = reportProcessedRepository.save(entity);
        return reportProcessedMapper.toDTO(entity);
    }

    @Override
    public void delete(Long id) {
        reportProcessedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        reportProcessedRepository.deleteById(id);
    }

    @Override
    public ReportProcessedDTO get(Long id) {
        ReportProcessedEntity entity = reportProcessedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        return reportProcessedMapper.toDTO(entity);
    }

    @Override
    public ReportProcessedDTO getByReportId(Long reportId) {
        ReportProcessedEntity entity = reportProcessedRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        return reportProcessedMapper.toDTO(entity);
    }

    @Override
    public Page<ReportProcessedDTO> list(Pageable pageable) {
        Page<ReportProcessedEntity> reports = reportProcessedRepository.findAllPage(pageable);
        return reports.map(reportProcessedMapper::toDTO);
    }
}
