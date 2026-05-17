package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportAnalysedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnalysedResponseDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionRequestDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;

// Interface de serviço de integração com o módulo de IA externo.
// Gerencia o pipeline de anonimização, classificação e sugestão de resposta para manifestações.
public interface ReportAiService {

    // Orquestra o pipeline completo de IA: anonimiza, classifica e persiste o resultado para a manifestação informada.
    void processReport(Long reportId);

    // Envia título e descrição ao serviço de IA para remoção de dados pessoais identificáveis.
    ReportAnonymizedResponseDTO anonymize(ReportAnonymizedRequestDTO report);

    // Envia a manifestação com arquivos e responsáveis ao serviço de IA para classificação e detecção de conflito.
    ReportAnalysedResponseDTO classify(ReportAnalysedRequestDTO report);

    // Solicita ao serviço de IA uma sugestão de resposta institucional para a manifestação.
    ReportResponseSuggestionResponseDTO suggestResponse(ReportResponseSuggestionRequestDTO report);
}
