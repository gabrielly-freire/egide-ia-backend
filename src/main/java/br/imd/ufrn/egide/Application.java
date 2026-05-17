package br.imd.ufrn.egide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// Ponto de entrada da aplicação Égide IA Backend.
// Habilita o agendamento de tarefas (@EnableScheduling) para o monitoramento de SLA.
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
