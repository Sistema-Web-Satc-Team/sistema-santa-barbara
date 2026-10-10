package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class EncerrarVinculoUseCase {

    private final MembroVinculoRepository membroVinculoRepository;

    public EncerrarVinculoUseCase(MembroVinculoRepository membroVinculoRepository) {
        this.membroVinculoRepository = membroVinculoRepository;
    }

    public void execute(UUID idMembro, int idVinculo) {
        MembroVinculo vinculo = membroVinculoRepository.findByIdAndMembroId(idVinculo, idMembro)
                .orElseThrow(() -> new NotFoundException("Vínculo não encontrado para este membro."));

        vinculo.encerrar();
    }
}
