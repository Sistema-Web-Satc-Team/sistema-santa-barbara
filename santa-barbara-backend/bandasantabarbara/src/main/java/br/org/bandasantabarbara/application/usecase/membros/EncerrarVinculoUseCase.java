package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.model.MembroVinculoId;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;

import java.util.UUID;

public class EncerrarVinculoUseCase {

    private final MembroVinculoRepository membroVinculoRepository;

    public EncerrarVinculoUseCase(MembroVinculoRepository membroVinculoRepository) {
        this.membroVinculoRepository = membroVinculoRepository;
    }

    public void execute(UUID idMembro, int idVinculo) {
        MembroVinculoId pk = new MembroVinculoId(idVinculo, idMembro);
        MembroVinculo vinculo = membroVinculoRepository.findById(pk)
                .orElseThrow(() -> new NotFoundException("Vínculo não encontrado para este membro."));

        vinculo.encerrar();
    }
}
