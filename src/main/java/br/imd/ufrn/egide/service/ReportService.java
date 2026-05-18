package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.OuvidorCaseDTO;
import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.dto.SatisfactionSurveyRequestDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// Interface de serviço central para o ciclo de vida das manifestações.
public interface ReportService {

    // Cria nova manifestação com arquivos; atribui ouvidor por sorteio e dispara evento de IA.
    ReportDTO save(ReportRequestDTO reportRequestDTO, List<MultipartFile> files);

    // Retorna todas as manifestações visíveis para o usuário autenticado.
    // Para MANAGER com conflito de interesse, título e descrição são substituídos pelas versões anonimizadas.
    List<ReportDTO> findAll();

    // Retorna apenas as manifestações registradas pelo usuário autenticado (REMONSTRANT).
    List<ReportDTO> findMyReports();

    // Retorna a manifestação pelo id em formato DTO; lança ResourceNotFoundException se não encontrada.
    ReportDTO getById(Long id);

    // Retorna a entidade da manifestação pelo id; utilizado internamente por outros services.
    ReportEntity findEntityById(Long id);

    // Retorna estatísticas agregadas para o dashboard: totais por status e médias de satisfação.
    Map<String, Object> getDashboardStatus();

    // Persiste pesquisa de satisfação vinculada à manifestação; impede duplicidade por manifestação.
    void saveSurvey(Long reportId, SatisfactionSurveyRequestDTO dto);

    // Retorna os casos atribuídos ao ouvidor autenticado; restrito a LISTENER e ADMIN.
    List<OuvidorCaseDTO> findCasesAssignedToCurrentOuvidor();

    // Retorna entidades de manifestação cujo status esteja na lista informada; usado em outros services de fase.
    List<ReportEntity> findEntitiesByStatusIn(List<ReportStatus> statuses);

    ReportDTO concluirRelato(Long id);
}
