package br.imd.ufrn.egide.event;

// Evento de domínio publicado pelo ReportServiceImpl após a persistência de uma nova manifestação.
// Consumido assincronamente pelo ReportCreatedListener para acionar o pipeline de análise de IA.
// Carrega apenas o id da manifestação para evitar detached entities no contexto assíncrono.
public record ReportCreatedEvent(Long reportId) {
}
