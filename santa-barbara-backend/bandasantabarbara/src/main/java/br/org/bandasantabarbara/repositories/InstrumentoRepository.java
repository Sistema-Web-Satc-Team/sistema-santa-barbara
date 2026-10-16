package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Instrumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentoRepository  extends JpaRepository<Instrumento, Integer> {

    List<Instrumento> findAllByNomeIn(List<String> nome);
}
