package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportProcessedDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Interface de serviço para gerenciamento de entidades de triagem pós-IA (ReportProcessedEntity).
public interface ReportProcessedService {

    // Persiste novo registro de triagem vinculado à manifestação informada no DTO.
    ReportProcessedDTO save(ReportProcessedDTO dto);

    // Atualiza os dados de triagem existentes pelo id; preserva o vínculo com a manifestação.
    ReportProcessedDTO update(Long id, ReportProcessedDTO dto);

    // Realiza soft-delete do registro de triagem pelo id.
    void delete(Long id);

    // Retorna o registro de triagem pelo id; lança ResourceNotFoundException se não encontrado.
    ReportProcessedDTO get(Long id);

    // Retorna o registro de triagem pelo id da manifestação; lança ResourceNotFoundException se não encontrado.
    ReportProcessedDTO getByReportId(Long reportId);

    // Retorna a listagem paginada de todos os registros de triagem ativos.
    Page<ReportProcessedDTO> list(Pageable pageable);
}
