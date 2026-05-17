package br.imd.ufrn.egide.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
// Propriedades de configuração para a conexão com o serviço de IA externo.
// Carregadas do application.properties/yaml com prefixo "ai" via @ConfigurationProperties.
// connectTimeoutMs e readTimeoutMs evitam bloqueio indefinido do thread pool de IA.
public record AiConnectionProperties(
        String baseUrl,
        String apiKey,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
