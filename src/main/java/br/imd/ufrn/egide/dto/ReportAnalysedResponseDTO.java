package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// DTO de resposta do serviço de IA com a classificação da manifestação.
// conflictedUserIds lista os IDs (string) dos usuários com conflito detectado.
// managerConflict indica especificamente se o conflito envolve um usuário com papel MANAGER,
// o que aciona a regra de ocultação de dados para gestores no ReportServiceImpl.
public record ReportAnalysedResponseDTO(
        @JsonProperty("report_id") Long reportId,
        ReportCategory category,
        @JsonProperty("risk_level") ReportRisk risk,
        @JsonProperty("conflict_detected") Boolean conflictDetected,
        @JsonProperty("conflicted_user_ids") List<String> conflictedUserIds,
        @JsonProperty("manager_conflict") Boolean managerConflict
) { }
