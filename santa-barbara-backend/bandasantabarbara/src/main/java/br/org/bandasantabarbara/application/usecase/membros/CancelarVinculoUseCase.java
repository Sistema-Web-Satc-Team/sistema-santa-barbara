package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelarVinculoUseCase {

    private final MembroVinculoRepository membroVinculoRepository;

    public CancelarVinculoUseCase(MembroVinculoRepository membroVinculoRepository) {
        this.membroVinculoRepository = membroVinculoRepository;
    }

    @Transactional
    public void cancelar(UUID membroId, int vinculoId) {
        var vinculo = this.membroVinculoRepository.findByIdAndMembroId(vinculoId, membroId).orElseThrow(() -> new NotFoundException("Vínculo ou membro inexistente."));

        vinculo.cancelar();
    }
}
