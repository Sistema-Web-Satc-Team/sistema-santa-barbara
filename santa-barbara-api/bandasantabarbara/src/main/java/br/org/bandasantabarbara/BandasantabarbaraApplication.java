package br.org.bandasantabarbara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = "br.org.bandasantabarbara.model")
@EnableJpaRepositories(basePackages = "br.org.bandasantabarbara.repositories")
public class BandasantabarbaraApplication {

	public static void main(String[] args) {
		// Dotenv dotenv = Dotenv.load();
		// dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(BandasantabarbaraApplication.class, args);
	}

}
