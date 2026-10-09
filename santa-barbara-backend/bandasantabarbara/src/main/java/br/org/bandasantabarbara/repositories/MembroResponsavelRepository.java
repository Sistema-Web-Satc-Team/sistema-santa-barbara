package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.model.MembroResponsavelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembroResponsavelRepository extends JpaRepository<MembroResponsavel, MembroResponsavelId> {

    List<MembroResponsavel> findByMembroMenorId(UUID idMembroMenor);

}
