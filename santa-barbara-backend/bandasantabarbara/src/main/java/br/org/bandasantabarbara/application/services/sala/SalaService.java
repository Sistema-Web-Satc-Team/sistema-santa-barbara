package br.org.bandasantabarbara.application.services.sala;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.mapper.SalaMapper;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Sala;
import br.org.bandasantabarbara.repositories.SalaRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SalaService {

    public SalaRepository salaRepository;
    public SalaMapper salaMapper;

    public SalaService( SalaRepository salaRepository, SalaMapper salaMapper ){
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
    }


    public SalaResponseDTO cadastrar(SalaRequestDTO dto) {

        this.salaRepository.findByCode(dto.code()).ifPresent(
                (other) -> {
                    throw new ConflictException("Já existe uma sala com o código '" + other.getCode() + "'.");
                }
        );

        Sala sala = this.salaMapper.toEntity(dto);

        Sala salaCadastrada = this.salaRepository.save(sala);

        return this.salaMapper.toResponse(salaCadastrada);
    }

    public SalaResponseDTO pegarPorCodigo(String codigo) {
        return this.salaMapper.toResponse(
            this.salaRepository.findByCode(codigo).orElseThrow(
                () -> new NotFoundException("Código de sala " + codigo + " não encontrado.")
            )
        );
    }

    public List<SalaResponseDTO> listar(Integer capacidade) {
        return this.salaRepository.findByCapacidade(capacidade).stream()
            .map(this.salaMapper::toResponse).toList();
        
    }
}
