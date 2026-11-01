package br.org.bandasantabarbara.application.services.turma;

import java.util.List;

import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.model.turma.Horario;

public class HorarioFactory {

    public static Horario criarApartirDe(List<HorarioRequestDTO> dtos) {
        Horario horario = new Horario();

        dtos.forEach(dto -> horario.adicionar(dto.diaDaSemana(), dto.inicio(), dto.fim()));
        
        return horario;
    }
}