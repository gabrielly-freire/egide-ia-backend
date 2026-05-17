package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO interno que representa um arquivo serializado em base64 para envio ao serviço de IA.
// O conteúdo base64Data é gerado a partir do arquivo físico no disco; nunca persiste no banco.
public record ReportAiFileProcessing(
        String filename,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("base64_data") String base64Data
) { }
