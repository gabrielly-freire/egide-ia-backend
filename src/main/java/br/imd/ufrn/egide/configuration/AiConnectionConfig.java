package br.imd.ufrn.egide.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AiConnectionProperties.class)
// Configuração do cliente HTTP (RestClient) utilizado para comunicação com o serviço de IA externo.
// Aplica timeouts de conexão e leitura definidos em AiConnectionProperties para evitar bloqueios no pool de IA.
// A chave de API é injetada como header padrão em todas as requisições ao microsserviço.
public class AiConnectionConfig {

    @Bean
    public RestClient aiRestClient(AiConnectionProperties prop) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(prop.connectTimeoutMs());
        factory.setReadTimeout(prop.readTimeoutMs());

        return RestClient.builder()
                .baseUrl(prop.baseUrl())
                .defaultHeader("API-Key", prop.apiKey())
                .requestFactory(factory)
                .build();
    }
}
