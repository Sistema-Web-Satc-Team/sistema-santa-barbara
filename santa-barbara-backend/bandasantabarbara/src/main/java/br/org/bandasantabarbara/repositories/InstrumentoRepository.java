package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Instrumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentoRepository  extends JpaRepository<Instrumento, Integer> {
}
