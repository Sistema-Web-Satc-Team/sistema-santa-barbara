package br.org.bandasantabarbara.application.services.turma;

import java.util.List;

import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.turma.Horario;

public class HorarioFactory {

    public static Horario criarApartirDe(List<HorarioRequestDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("A turma deve ter ao menos um horário.");
        }
        if (dtos.stream().anyMatch(java.util.Objects::isNull)) {
            throw new BadRequestException("A lista de horários contém um item inválido.");
        }

        Horario horario = new Horario();

        dtos.forEach(dto -> horario.adicionar(dto.diaDaSemana(), dto.inicio(), dto.fim()));
        
        return horario;
    }
}