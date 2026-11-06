package br.org.bandasantabarbara.application.services.musica;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.model.musica.Genero;
import br.org.bandasantabarbara.repositories.musica.GeneroRepository;

@Service
public class GeneroService {
    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @Transactional(readOnly = true)
    public List<String> sugerir(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }

        String termo = q.strip().replaceAll("\\s+", " ");

        return this.generoRepository.buscarPorTermo(termo, PageRequest.of(0, 10)).stream().map(Genero::getNome).toList();
    }
}
