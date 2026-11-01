package br.org.bandasantabarbara.application.mapper;

import java.util.List;

import br.org.bandasantabarbara.application.dtos.professores.InstrumentoProfessorResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorResponse;
import br.org.bandasantabarbara.repositories.ensino.ProfessorProjection;
import org.springframework.stereotype.Component;

@Component
public class ProfessorMapper {

    public ProfessorResponse toResponse(
            ProfessorProjection professor,
            boolean temTurma,
            List<String> instrumentos
    ) {
        return new ProfessorResponse(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getTelefone(),
                professor.getSexo(),
                temTurma,
                new InstrumentoProfessorResponse(instrumentos)
        );
    }
}
