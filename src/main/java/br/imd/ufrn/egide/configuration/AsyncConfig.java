package br.imd.ufrn.egide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
// Configuração do pool de threads assíncronas da aplicação.
// O bean "aiExecutor" é utilizado pelo ReportCreatedListener para processar o pipeline de IA
// sem bloquear a thread da requisição HTTP que criou a manifestação.
public class AsyncConfig {
    @Bean(name = "aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ai-worker-");
        executor.initialize();
        return executor;
    }
}
