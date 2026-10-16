package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.model.Genero;
import br.org.bandasantabarbara.repositories.GeneroRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SugerirGeneroUseCase {

    private final GeneroRepository generoRepository;

    public SugerirGeneroUseCase(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    public List<String> sugerir(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }

        String termo = q.strip().replaceAll("\\s+", " ");

        return this.generoRepository.buscarPorTermo(termo, PageRequest.of(0, 10)).stream().map(Genero::getNome).toList();
    }
}
