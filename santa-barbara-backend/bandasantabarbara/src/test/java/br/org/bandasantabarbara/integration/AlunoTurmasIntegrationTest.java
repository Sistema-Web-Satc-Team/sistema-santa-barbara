package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoHorarioIntervaloResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoTurmaResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoTurmaHorarioResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorHorarioIntervaloResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorTurmaAtualResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorTurmaHorarioResponse;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroRelacaoRequest;
import br.org.bandasantabarbara.application.services.ensino.AlunoService;
import br.org.bandasantabarbara.application.services.ensino.ProfessorService;
import br.org.bandasantabarbara.application.services.membro.MembroService;
import br.org.bandasantabarbara.controller.ProfessorController;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import br.org.bandasantabarbara.model.musica.Instrumento;
import br.org.bandasantabarbara.model.turma.InstrumentoTurma;
import br.org.bandasantabarbara.model.turma.Horario;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AlunoTurmasIntegrationTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorController professorController;

    @Autowired
    private MembroService membroService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void listaTurmasAtuaisComProfessorSalaInstrumentosEColegasEAvaliaAcessoDoResponsavel() {
        Membro alunoMembro = cadastrarMembro("aluno-lista-turmas");
        Membro colegaMembro = cadastrarMembro("colega-lista-turmas");
        Membro professorMembro = cadastrarMembro("professor-lista-turmas");
        Membro responsavel = cadastrarMembro("responsavel-lista-turmas");
        Membro outroMembro = cadastrarMembro("outro-lista-turmas");

        Aluno aluno = cadastrarAluno(alunoMembro);
        Aluno colega = cadastrarAluno(colegaMembro);
        Professor professor = cadastrarProfessor(professorMembro);
        membroService.adicionarResponsavelAoDependente(
                responsavel.getId(),
                alunoMembro.getId(),
                new MembroRelacaoRequest(MembroResponsavel.TipoRelacaoResponsavel.RESPONSAVEL_LEGAL)
        );

        Sala sala = new Sala("sala-lista-turmas", 10);
        entityManager.persist(sala);
        Instrumento instrumento = new Instrumento("Clarinete", Instrumento.TipoInstrumentoEnum.SOPRO);
        entityManager.persist(instrumento);
        Turma turma = new Turma("turma de clarinete", sala);
        turma.setNumeroVagas(10);
        Horario horario = new Horario();
        horario.adicionar(DayOfWeek.MONDAY, LocalTime.of(19, 0), LocalTime.of(20, 0));
        turma.configurarHorario(Turma.EnumTipoTurno.NOTURNO, horario);
        turma.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(30));
        entityManager.persist(turma);
        entityManager.flush();

        entityManager.persist(new MatriculaAlunoTurma(turma, aluno, LocalDate.now()));
        entityManager.persist(new MatriculaAlunoTurma(turma, colega, LocalDate.now()));
        entityManager.persist(new ProfessorTurma(turma, professor, LocalDate.now()));
        entityManager.persist(new InstrumentoTurma(turma, instrumento));
        entityManager.flush();

        List<AlunoTurmaResponse> turmas = alunoService.listarTurmasAtuais(aluno.getId());

        assertThat(turmas).singleElement().satisfies(turmaResponse -> {
            assertThat(turmaResponse.turma()).isEqualTo("turma de clarinete");
            assertThat(turmaResponse.professor()).isEqualTo("professor-lista-turmas");
            assertThat(turmaResponse.sala()).isEqualTo("sala-lista-turmas");
            assertThat(turmaResponse.turno()).isEqualTo(Turma.EnumTipoTurno.NOTURNO);
            assertThat(turmaResponse.instrumentos()).containsExactly("clarinete");
            assertThat(turmaResponse.numeroDeColegas()).isEqualTo(1);
            assertThat(turmaResponse.colegas()).containsExactly("colega-lista-turmas");
        });
        PageResponse<AlunoTurmaHorarioResponse> horarios = alunoService.listarHorariosAtuais(
                aluno.getId(),
                PageRequest.of(0, 20, Sort.by("turma").ascending())
        );
        assertThat(horarios.content()).singleElement().satisfies(turmaHorario -> {
            assertThat(turmaHorario.turma()).isEqualTo("turma de clarinete");
            assertThat(turmaHorario.professor()).isEqualTo("professor-lista-turmas");
            assertThat(turmaHorario.sala()).isEqualTo("sala-lista-turmas");
            assertThat(turmaHorario.turno()).isEqualTo(Turma.EnumTipoTurno.NOTURNO);
            assertThat(turmaHorario.horarios()).containsExactly(
                    new AlunoHorarioIntervaloResponse(
                            "SEGUNDA", LocalTime.of(19, 0), LocalTime.of(20, 0)
                    )
            );
        });
        assertThat(alunoService.podeVisualizarTurmas(aluno.getId(), aluno.getId())).isTrue();
        assertThat(alunoService.podeVisualizarTurmas(responsavel.getId(), aluno.getId())).isTrue();
        assertThat(alunoService.podeVisualizarTurmas(outroMembro.getId(), aluno.getId())).isFalse();

        PageResponse<ProfessorTurmaAtualResponse> turmasDoProfessor = professorService.listarTurmasAtuais(
                professor.getId(),
                PageRequest.of(0, 20, Sort.by("turma").ascending())
        );
        assertThat(turmasDoProfessor.content()).singleElement().satisfies(turmaProfessor -> {
            assertThat(turmaProfessor.turma()).isEqualTo("turma de clarinete");
            assertThat(turmaProfessor.professor()).isEqualTo("professor-lista-turmas");
            assertThat(turmaProfessor.sala()).isEqualTo("sala-lista-turmas");
            assertThat(turmaProfessor.turno()).isEqualTo(Turma.EnumTipoTurno.NOTURNO);
            assertThat(turmaProfessor.alunos()).containsExactly(
                    "aluno-lista-turmas",
                    "colega-lista-turmas"
            );
            assertThat(turmaProfessor.instrumentos()).containsExactly("clarinete");
        });

        PageResponse<ProfessorTurmaHorarioResponse> horariosDoProfessor = professorService.listarHorariosAtuais(
                professor.getId(),
                PageRequest.of(0, 20, Sort.by("turma").ascending())
        );
        assertThat(horariosDoProfessor.content()).singleElement().satisfies(turmaHorario -> {
            assertThat(turmaHorario.turma()).isEqualTo("turma de clarinete");
            assertThat(turmaHorario.professor()).isEqualTo("professor-lista-turmas");
            assertThat(turmaHorario.sala()).isEqualTo("sala-lista-turmas");
            assertThat(turmaHorario.turno()).isEqualTo(Turma.EnumTipoTurno.NOTURNO);
            assertThat(turmaHorario.horarios()).containsExactly(
                    new ProfessorHorarioIntervaloResponse(
                            "SEGUNDA", LocalTime.of(19, 0), LocalTime.of(20, 0)
                    )
            );
        });
    }

    // Ok esse teste não deveria estar aqui! kkkk
    @Test
    @WithMockUser(authorities = "PROFESSOR")
    void professorSoPodeConsultarAsPropriasTurmas() {
        UUID idProfessorAutenticado = UUID.randomUUID();
        UUID idOutroProfessor = UUID.randomUUID();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(idProfessorAutenticado.toString())
                .build();

        assertThat(org.assertj.core.api.Assertions.catchThrowable(() ->
                professorController.listarTurmasAtuais(
                        idOutroProfessor,
                        PageRequest.of(0, 20),
                        jwt
                )
        )).isInstanceOf(ForbiddenException.class);
        assertThat(org.assertj.core.api.Assertions.catchThrowable(() ->
                professorController.listarHorariosAtuais(
                        idOutroProfessor,
                        PageRequest.of(0, 20),
                        jwt
                )
        )).isInstanceOf(ForbiddenException.class);
    }

    private Membro cadastrarMembro(String nome) {
        Membro membro = new Membro();
        membro.setNome(nome);
        membro.setDataNascimento(LocalDate.now().minusYears(20));
        membro.setSexo(Membro.MembroSexoEnum.MASCULINO);
        membro.setEmail(nome + "-" + membro.getId() + "@example.test");
        membro.setTelefone("48000000001");
        entityManager.persist(membro);
        return membro;
    }

    private Aluno cadastrarAluno(Membro membro) {
        Aluno aluno = new Aluno();
        aluno.setMembro(membro);
        entityManager.persist(aluno);
        return aluno;
    }

    private Professor cadastrarProfessor(Membro membro) {
        Professor professor = new Professor();
        professor.setMembro(membro);
        entityManager.persist(professor);
        return professor;
    }

}
