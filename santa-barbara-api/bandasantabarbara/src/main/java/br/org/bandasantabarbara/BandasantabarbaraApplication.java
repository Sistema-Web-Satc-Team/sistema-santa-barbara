package br.org.bandasantabarbara;

import br.org.bandasantabarbara.application.RegisterAdminDTO;
import br.org.bandasantabarbara.application.SetupApplicationUsecase;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "br.org.bandasantabarbara.model")
@EnableJpaRepositories(basePackages = "br.org.bandasantabarbara.repositories")
public class BandasantabarbaraApplication {

	public static void main(String[] args) {
		SpringApplication.run(BandasantabarbaraApplication.class, args);
	}

}
