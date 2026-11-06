package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.mapper.SalaMapper;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.repositories.turma.SalaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final TurmaRepository turmaRepository;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper, TurmaRepository turmaRepository) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.turmaRepository = turmaRepository;
    }


    @Transactional
    public SalaResponseDTO cadastrar(SalaRequestDTO dto) {

        if (this.salaRepository.findByCode(dto.code()).isPresent()) {
            throw new ConflictException("Já existe uma sala com o código '" + dto.code() + "'.");
        }

        Sala sala = this.salaMapper.toEntity(dto);

        Sala salaCadastrada = this.salaRepository.save(sala);

        return this.salaMapper.toResponse(salaCadastrada);
    }

    @Transactional
    public SalaResponseDTO atualizar(String codigoAtual, SalaRequestDTO dto) {
        Sala sala = salaRepository.findByCodeForUpdate(codigoAtual)
            .orElseThrow(() -> new NotFoundException("Código de sala " + codigoAtual + " não encontrado."));

        if (salaRepository.existsByCodeAndIdNot(dto.code(), sala.getId())) {
            throw new ConflictException("Já existe uma sala com o código '" + dto.code() + "'.");
        }

        if (turmaRepository.existsBySala_IdAndNumeroVagasGreaterThan(sala.getId(), dto.capacidade())) {
            throw new ConflictException(
                "A capacidade não pode ser menor que o número de vagas de uma turma associada."
            );
        }

        sala.atualizar(dto.code(), dto.capacidade());
        return salaMapper.toResponse(salaRepository.save(sala));
    }

    @Transactional
    public void deletar(String codigo) {
        Sala sala = salaRepository.findByCodeForUpdate(codigo)
            .orElseThrow(() -> new NotFoundException("Código de sala " + codigo + " não encontrado."));

        if (turmaRepository.existsBySala_Id(sala.getId())) {
            throw new ConflictException("Não é possível excluir uma sala associada a uma turma.");
        }

        salaRepository.delete(sala);
    }

    @Transactional(readOnly = true)
    public SalaResponseDTO pegarPorCodigo(String codigo) {
        return this.salaMapper.toResponse(
            buscarEntidadePorCodigo(codigo)
        );
    }

    @Transactional(readOnly = true)
    public Sala buscarEntidadePorCodigo(String codigo) {
        return this.salaRepository.findByCode(codigo)
            .orElseThrow(() -> new NotFoundException("Código de sala " + codigo + " não encontrado."));
    }

    @Transactional
    public Sala buscarEntidadePorCodigoParaAgendamento(String codigo) {
        return this.salaRepository.findByCodeForUpdate(codigo)
            .orElseThrow(() -> new NotFoundException("Código de sala " + codigo + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listar(Integer capacidade) {
        List<Sala> salas = capacidade == null
            ? salaRepository.findAll()
            : salaRepository.findByCapacidade(capacidade);
        return salas.stream()
            .map(this.salaMapper::toResponse).toList();
        
    }
}
