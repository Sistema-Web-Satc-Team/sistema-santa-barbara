package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.mapper.VinculoMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarVinculosUseCase {

    private final MembroRepository membroRepository;
    private final MembroVinculoRepository membroVinculoRepository;
    private final VinculoMapper vinculoMapper;

    public ConsultarVinculosUseCase(
            MembroRepository membroRepository,
            MembroVinculoRepository membroVinculoRepository,
            VinculoMapper vinculoMapper) {
        this.membroRepository = membroRepository;
        this.membroVinculoRepository = membroVinculoRepository;
        this.vinculoMapper = vinculoMapper;
    }

    public MembroVinculosResponse execute(UUID membroId) {

        Membro membro = this.membroRepository.findById(membroId)
                .orElseThrow(() -> new NotFoundException("Membro não encontado."));

        var vinculosAtuais = this.membroVinculoRepository.findByMembroIdAndDataTerminoIsNullOrderByDataInicioDesc(membroId);
        var vinculosEncerrados = this.membroVinculoRepository.findByMembroIdAndDataTerminoIsNotNullOrderByDataInicioDesc(membroId);

        var vinculos = new VinculosResponse(
                vinculosAtuais.stream().map(vinculoMapper::toResponse).toList(),
                vinculosEncerrados.stream().map(vinculoMapper::toResponse).toList()
        );

        return new MembroVinculosResponse(
                membro.getId(),
                membro.getNome(),
                membro.getDataNascimento(),
                membro.getIdade(),
                membro.getSexo(),
                vinculos
        );
    }
}
