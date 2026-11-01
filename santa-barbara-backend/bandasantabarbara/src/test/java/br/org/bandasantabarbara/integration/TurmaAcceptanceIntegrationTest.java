package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TurmaAcceptanceIntegrationTest {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private SalaService salaService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void turmaPossuiUmaSalaAssociada() {
        cadastrarSala("sala-unica");

        TurmaResponseDTO turma = criarTurma("sala-unica", "turma com sala única", DayOfWeek.MONDAY, 10, 11);

        assertThat(turma.sala()).isNotNull();
        assertThat(turma.sala().code()).isEqualTo("sala-unica");
        assertThat(turma.sala().id()).isNotNull();
    }

    @Test
    void naoPermiteVagasAcimaDaCapacidadeDaSala() {
        cadastrarSala("sala-capacidade");
        LocalDate inicio = LocalDate.now().plusDays(7);

        assertThatThrownBy(() -> turmaService.criar(new CriarTurmaRequestDTO(
            "sala-capacidade",
            13,
            inicio,
            inicio.plusDays(20),
            "turma acima da capacidade",
            Turma.EnumTipoTurno.MATUTINO,
            List.of(horario(DayOfWeek.MONDAY, 10, 11))
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("vagas não pode exceder a capacidade máxima");
    }

    @Test
    void naoPermiteCriarTurmaComHorarioForaDoTurno() {
        cadastrarSala("sala-turno");
        LocalDate inicio = LocalDate.now().plusDays(7);

        assertThatThrownBy(() -> turmaService.criar(new CriarTurmaRequestDTO(
            "sala-turno",
            10,
            inicio,
            inicio.plusDays(20),
            "turma fora do turno",
            Turma.EnumTipoTurno.MATUTINO,
            List.of(horario(DayOfWeek.MONDAY, 11, 13))
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("dentro do turno MATUTINO");
    }

    @Test
    void naoPermiteIntervalosSobrepostosNoCalendarioDaMesmaTurma() {
        cadastrarSala("sala-horario");
        LocalDate inicio = LocalDate.now().plusDays(7);

        assertThatThrownBy(() -> turmaService.criar(new CriarTurmaRequestDTO(
            "sala-horario",
            10,
            inicio,
            inicio.plusDays(20),
            "turma com horários sobrepostos",
            Turma.EnumTipoTurno.MATUTINO,
            List.of(
                horario(DayOfWeek.MONDAY, 10, 13),
                horario(DayOfWeek.MONDAY, 12, 14)
            )
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("colide com um horário já existente");
    }

    @Test
    void permiteIntervalosAdjacentesNoCalendarioDaTurma() {
        cadastrarSala("sala-adjacente");

        TurmaResponseDTO turma = criarTurma(
            "sala-adjacente",
            "turma com horários adjacentes",
            DayOfWeek.MONDAY,
            10,
            11,
            horario(DayOfWeek.MONDAY, 11, 12)
        );

        assertThat(turma.horarios().segunda()).hasSize(2);
    }

    @Test
    void naoPermiteQueProfessorSejaAlunoDaMesmaTurma() {
        cadastrarSala("sala-professor-aluno");
        UUID membroId = cadastrarMembro("professor-aluno");
        Aluno aluno = cadastrarAluno(membroId);
        Professor professor = cadastrarProfessor(membroId);
        Long turmaId = criarTurma(
            "sala-professor-aluno",
            "turma professor aluno",
            DayOfWeek.MONDAY,
            19,
            20
        ).id();
        Turma turma = entityManager.find(Turma.class, turmaId);
        entityManager.persist(new ProfessorTurma(turma, professor, LocalDate.now()));
        entityManager.flush();

        assertThatThrownBy(() -> turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaId)))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("professor não pode ser aluno");
    }

    @Test
    void naoPermiteMatricularAlunoDuasVezesNaMesmaTurma() {
        cadastrarSala("sala-matricula-dupla");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-duplicado"));
        Long turmaId = criarTurma(
            "sala-matricula-dupla",
            "turma matrícula dupla",
            DayOfWeek.MONDAY,
            19,
            20
        ).id();
        MatriculaRequestDTO request = new MatriculaRequestDTO(aluno.getId(), turmaId);

        turmaService.matricular(request);

        assertThatThrownBy(() -> turmaService.matricular(request))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("matrícula ativa nesta turma");
    }

    @Test
    void naoPermiteMatricularAlunoEmOutraTurmaComColisaoDeDiaEHorario() {
        cadastrarSala("sala-colisao-a");
        cadastrarSala("sala-colisao-b");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-colisao"));
        Long turmaAId = criarTurma("sala-colisao-a", "turma a", DayOfWeek.MONDAY, 19, 20).id();
        LocalDate dataInicio = LocalDate.now().plusDays(7);
        Long turmaBId = turmaService.criar(new CriarTurmaRequestDTO(
            "sala-colisao-b",
            10,
            dataInicio,
            dataInicio.plusDays(30),
            "turma b",
            Turma.EnumTipoTurno.NOTURNO,
            List.of(new HorarioRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(19, 30),
                LocalTime.of(20, 30)
            ))
        )).id();

        turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));

        assertThatThrownBy(() -> turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId)))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("conflito de horários");
    }

    @Test
    void permiteMatricularAlunoEmOutroDiaNoMesmoHorario() {
        cadastrarSala("sala-outro-dia-a");
        cadastrarSala("sala-outro-dia-b");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-outro-dia"));
        Long turmaAId = criarTurma("sala-outro-dia-a", "turma segunda", DayOfWeek.MONDAY, 19, 20).id();
        Long turmaBId = criarTurma("sala-outro-dia-b", "turma terça", DayOfWeek.TUESDAY, 19, 20).id();

        turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));
        var matricula = turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId));

        assertThat(matricula.id()).isNotNull();
        assertThat(matricula.idTurma()).isEqualTo(turmaBId);
    }

    @Test
    void permiteMatricularAlunoEmIntervalosAdjacentesDeTurmasDiferentes() {
        cadastrarSala("sala-adjacente-a");
        cadastrarSala("sala-adjacente-b");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-adjacente"));
        Long turmaAId = criarTurma("sala-adjacente-a", "turma até 20h", DayOfWeek.MONDAY, 19, 20).id();
        Long turmaBId = criarTurma("sala-adjacente-b", "turma a partir de 20h", DayOfWeek.MONDAY, 20, 21).id();

        turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));
        var matricula = turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId));

        assertThat(matricula.id()).isNotNull();
        assertThat(matricula.idTurma()).isEqualTo(turmaBId);
    }

    @Test
    void naoPermiteVincularComoProfessorQuemJaEAlunoDaTurma() {
        cadastrarSala("sala-aluno-professor");
        UUID membroId = cadastrarMembro("aluno-professor");
        Aluno aluno = cadastrarAluno(membroId);
        Professor professor = cadastrarProfessor(membroId);
        Long turmaId = criarTurma(
            "sala-aluno-professor",
            "turma aluno professor",
            DayOfWeek.MONDAY,
            19,
            20
        ).id();
        turmaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaId));

        assertThatThrownBy(() -> turmaService.vincularProfessor(turmaId, professor.getId()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("professor não pode ser aluno");
    }

    private void cadastrarSala(String codigo) {
        salaService.cadastrar(new SalaRequestDTO(codigo, 12));
    }

    private TurmaResponseDTO criarTurma(
        String codigoSala,
        String nome,
        DayOfWeek dia,
        int horaInicio,
        int horaFim,
        HorarioRequestDTO... horariosAdicionais
    ) {
        LocalDate dataInicio = LocalDate.now().plusDays(7);
        var horarios = new java.util.ArrayList<HorarioRequestDTO>();
        horarios.add(horario(dia, horaInicio, horaFim));
        horarios.addAll(List.of(horariosAdicionais));

        return turmaService.criar(new CriarTurmaRequestDTO(
            codigoSala,
            10,
            dataInicio,
            dataInicio.plusDays(30),
            nome,
            horaInicio >= 18 ? Turma.EnumTipoTurno.NOTURNO : Turma.EnumTipoTurno.MATUTINO,
            horarios
        ));
    }

    private HorarioRequestDTO horario(DayOfWeek dia, int inicio, int fim) {
        return new HorarioRequestDTO(dia, LocalTime.of(inicio, 0), LocalTime.of(fim, 0));
    }

    private UUID cadastrarMembro(String prefixoEmail) {
        Membro membro = new Membro();
        membro.setNome(prefixoEmail);
        membro.setDataNascimento(LocalDate.now().minusYears(20));
        membro.setSexo(Membro.MembroSexoEnum.MASCULINO);
        membro.setEmail(prefixoEmail + "-" + UUID.randomUUID() + "@example.test");
        membro.setTelefone("48000000001");
        entityManager.persist(membro);
        entityManager.flush();
        return membro.getId();
    }

    private Aluno cadastrarAluno(UUID membroId) {
        Aluno aluno = new Aluno();
        aluno.setMembro(entityManager.find(Membro.class, membroId));
        entityManager.persist(aluno);
        entityManager.flush();
        return aluno;
    }

    private Professor cadastrarProfessor(UUID membroId) {
        Professor professor = new Professor();
        professor.setMembro(entityManager.find(Membro.class, membroId));
        entityManager.persist(professor);
        entityManager.flush();
        return professor;
    }
}
