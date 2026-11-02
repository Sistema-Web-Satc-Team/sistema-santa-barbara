package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.turmas.MatriculaDetalheResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaAlunoResponseDTO;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class MatriculaMapper {

    public MatriculaResponseDTO toResponse(MatriculaAlunoTurma matricula) {
        return new MatriculaResponseDTO(
            matricula.getId(),
            matricula.getAluno().getId(),
            matricula.getTurma().getId(),
            matricula.getStatus().toString(),
            matricula.getDataMatricula(),
            matricula.getDataSaida(),
            matricula.getJustificativaMudancaStatus()
        );
    }

    public MatriculaDetalheResponseDTO toDetailResponse(MatriculaAlunoTurma matricula) {
        var aluno = matricula.getAluno();
        var membro = aluno.getMembro();
        return new MatriculaDetalheResponseDTO(
            matricula.getId(),
            matricula.getTurma().getId(),
            aluno.getId(),
            membro.getNome(),
            membro.getTelefone(),
            membro.getEmail(),
            membro.getDataNascimento().plusYears(18).isAfter(LocalDate.now()),
            matricula.getStatus().toString(),
            matricula.getJustificativaMudancaStatus()
        );
    }

    public TurmaAlunoResponseDTO toTurmaAlunoResponse(MatriculaAlunoTurma matricula) {
        var aluno = matricula.getAluno();
        var membro = aluno.getMembro();
        return new TurmaAlunoResponseDTO(
            matricula.getId(),
            aluno.getId(),
            membro.getNome(),
            membro.getTelefone(),
            membro.getEmail(),
            membro.getDataNascimento().plusYears(18).isAfter(LocalDate.now())
        );
    }
}
