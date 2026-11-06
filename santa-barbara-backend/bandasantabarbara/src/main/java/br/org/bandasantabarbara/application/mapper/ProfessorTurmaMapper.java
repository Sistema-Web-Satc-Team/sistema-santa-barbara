package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaProfessorResponseDTO;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import org.springframework.stereotype.Component;

@Component
public class ProfessorTurmaMapper {

    public ProfessorTurmaResponseDTO toResponse(ProfessorTurma vinculo) {
        var professor = vinculo.getProfessor();
        var membro = professor.getMembro();

        return new ProfessorTurmaResponseDTO(
            vinculo.getId(),
            professor.getId(),
            vinculo.getTurma().getId(),
            vinculo.getStatus().toString(),
            membro.getNome(),
            membro.getTelefone(),
            membro.getEmail(),
            vinculo.getDataEntrada(),
            vinculo.getDataSaida(),
            vinculo.getJustificativa()
        );
    }

    public TurmaProfessorResponseDTO toTurmaResponse(ProfessorTurma vinculo) {
        var professor = vinculo.getProfessor();
        var membro = professor.getMembro();
        return new TurmaProfessorResponseDTO(
            vinculo.getId(),
            professor.getId(),
            membro.getNome(),
            membro.getTelefone(),
            membro.getEmail()
        );
    }
}
