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
// Implementação de ReportProcessedService; gerencia o ciclo de vida dos registros de triagem pós-IA.
public class ReportProcessedServiceImpl implements ReportProcessedService {

    private final ReportProcessedRepository reportProcessedRepository;
    private final ReportRepository reportRepository;
    private final ReportProcessedMapper reportProcessedMapper;

    // Cria e persiste novo registro de triagem; valida existência da manifestação antes de salvar.
    @Override
    public ReportProcessedDTO save(ReportProcessedDTO dto) {
        ReportEntity report = reportRepository.findById(dto.reportId())
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        ReportProcessedEntity entity = reportProcessedMapper.toEntity(dto);
        entity.setReport(report);
        entity = reportProcessedRepository.save(entity);
        return reportProcessedMapper.toDTO(entity);
    }

    // Atualiza o registro de triagem; valida existência do registro e da manifestação antes de salvar.
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

    // Realiza soft-delete do registro de triagem; valida existência antes de excluir.
    @Override
    public void delete(Long id) {
        reportProcessedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        reportProcessedRepository.deleteById(id);
    }

    // Busca e retorna o registro de triagem pelo id; lança ResourceNotFoundException se não encontrado.
    @Override
    public ReportProcessedDTO get(Long id) {
        ReportProcessedEntity entity = reportProcessedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        return reportProcessedMapper.toDTO(entity);
    }

    // Busca o registro de triagem pelo id da manifestação; lança ResourceNotFoundException se não encontrado.
    @Override
    public ReportProcessedDTO getByReportId(Long reportId) {
        ReportProcessedEntity entity = reportProcessedRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia processada não encontrada"));

        return reportProcessedMapper.toDTO(entity);
    }

    // Retorna listagem paginada de todos os registros de triagem ativos.
    @Override
    public Page<ReportProcessedDTO> list(Pageable pageable) {
        Page<ReportProcessedEntity> reports = reportProcessedRepository.findAllPage(pageable);
        return reports.map(reportProcessedMapper::toDTO);
    }
}
