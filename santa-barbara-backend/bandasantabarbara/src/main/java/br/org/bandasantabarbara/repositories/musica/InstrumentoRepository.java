package br.org.bandasantabarbara.repositories.musica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.org.bandasantabarbara.model.musica.Instrumento;

import java.util.List;

@Repository
public interface InstrumentoRepository  extends JpaRepository<Instrumento, Integer> {

    List<Instrumento> findAllByNomeIn(List<String> nome);
}
