package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.model.Sala;
import org.springframework.stereotype.Component;

@Component
public class SalaMapper {

    public Sala toEntity(SalaRequestDTO request) {
        return new Sala(request.code(), request.capacidade());
    }

    public SalaResponseDTO toResponse(Sala sala) {
        return new SalaResponseDTO(sala.getId(), sala.getCode(), sala.getCapacidade());
    }
}
