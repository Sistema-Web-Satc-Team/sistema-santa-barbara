package br.org.bandasantabarbara.application.mapper;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponsavelResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.dtos.alunos.InstrumentoAlunoResponse;
import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import br.org.bandasantabarbara.repositories.ensino.AlunoProjection;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapper {

    private final GrauRelacaoMapper grauRelacaoMapper;

    public AlunoMapper(GrauRelacaoMapper grauRelacaoMapper) {
        this.grauRelacaoMapper = grauRelacaoMapper;
    }

    public AlunoResponse toResponse(
            AlunoProjection aluno,
            boolean temTurma,
            List<String> instrumentos,
            List<MembroResponsavel> responsaveis
    ) {
        var responsaveisResponse = responsaveis.stream()
                .map(responsavel -> new AlunoResponsavelResponse(
                        responsavel.getResponsavel().getId(),
                        responsavel.getResponsavel().getNome(),
                        responsavel.getResponsavel().getEmail(),
                        responsavel.getResponsavel().getTelefone(),
                        grauRelacaoMapper.toResponse(
                                responsavel.getResponsavel().getSexo(),
                                responsavel.getTipoRelacao()
                        )
                ))
                .toList();

        int idade = Period.between(aluno.getDataNascimento(), LocalDate.now()).getYears();

        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                aluno.getSexo(),
                idade,
                temTurma,
                new InstrumentoAlunoResponse(instrumentos),
                responsaveisResponse
        );
    }
}
