package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TransferirMatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.MatriculaService;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
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
    private MatriculaService matriculaService;

    @Autowired
    private SalaService salaService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MatriculaAlunoTurmaRepository matriculaRepository;

    @Autowired
    private ProfessorTurmaRepository professorTurmaRepository;

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

        assertThatThrownBy(() -> turmaService.criar(new CriarTurmaRequestDTO(
            "sala-capacidade",
            13,
            "turma acima da capacidade",
            Turma.EnumTipoTurno.MATUTINO,
            null
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("vagas não pode exceder a capacidade máxima");
    }

    @Test
    void naoPermiteDefinirPeriodoSemProfessorAtivo() {
        cadastrarSala("sala-periodo-sem-professor");
        TurmaResponseDTO turma = criarTurmaBasica(
            "sala-periodo-sem-professor",
            "turma sem professor",
            Turma.EnumTipoTurno.NOTURNO
        );
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-sem-professor"));
        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turma.id()));

        assertThatThrownBy(() -> configurarPeriodo(
            turma.id(),
            LocalDate.now().plusDays(7),
            List.of(horario(DayOfWeek.MONDAY, 19, 20))
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Vincule ao menos um professor");
    }

    @Test
    void naoPermiteDefinirPeriodoSemAlunoAtivo() {
        cadastrarSala("sala-periodo-sem-aluno");
        TurmaResponseDTO turma = criarTurmaBasica(
            "sala-periodo-sem-aluno",
            "turma sem aluno",
            Turma.EnumTipoTurno.NOTURNO
        );
        Professor professor = cadastrarProfessor(cadastrarMembro("professor-sem-aluno"));
        turmaService.vincularProfessor(turma.id(), professor.getId());

        assertThatThrownBy(() -> configurarPeriodo(
            turma.id(),
            LocalDate.now().plusDays(7),
            List.of(horario(DayOfWeek.MONDAY, 19, 20))
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Matricule ao menos um aluno");
    }

    @Test
    void naoPermiteReservarSalaParaPeriodosSobrepostosConfiguradosViaPatch() {
        cadastrarSala("sala-periodo-conflito");
        LocalDate inicio = LocalDate.now().plusDays(7);
        TurmaResponseDTO primeira = criarTurmaComInicio(
            "sala-periodo-conflito",
            "primeira turma",
            inicio,
            DayOfWeek.MONDAY,
            19,
            20
        );
        TurmaResponseDTO segunda = criarTurmaBasica(
            "sala-periodo-conflito",
            "segunda turma",
            Turma.EnumTipoTurno.NOTURNO
        );
        adicionarParticipantes(segunda.id());

        assertThatThrownBy(() -> configurarPeriodo(
            segunda.id(),
            primeira.dataInicio(),
            List.of(horario(DayOfWeek.TUESDAY, 19, 20))
        ))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("sala já está alocada");
    }

    @Test
    void naoPermiteCriarTurmaComHorarioForaDoTurno() {
        cadastrarSala("sala-turno");

        assertThatThrownBy(() -> criarTurma(
            "sala-turno",
            "turma fora do turno",
            DayOfWeek.MONDAY,
            11,
            13
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("dentro do turno MATUTINO");
    }

    @Test
    void naoPermiteIntervalosSobrepostosNoCalendarioDaMesmaTurma() {
        cadastrarSala("sala-horario");

        assertThatThrownBy(() -> criarTurma(
            "sala-horario",
            "turma com horários sobrepostos",
            DayOfWeek.MONDAY,
            10,
            13,
            horario(DayOfWeek.MONDAY, 12, 14)
        ))
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

        assertThatThrownBy(() -> matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaId)))
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

        matriculaService.matricular(request);

        assertThatThrownBy(() -> matriculaService.matricular(request))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("matrícula ativa nesta turma");
    }

    @Test
    void naoPermiteMatricularAlunoEmOutraTurmaComColisaoDeDiaEHorario() {
        cadastrarSala("sala-colisao-a");
        cadastrarSala("sala-colisao-b");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-colisao"));
        Long turmaAId = criarTurma("sala-colisao-a", "turma a", DayOfWeek.MONDAY, 19, 20).id();
        Long turmaBId = criarTurmaComHorarios(
            "sala-colisao-b",
            "turma b",
            LocalDate.now().plusDays(7),
            Turma.EnumTipoTurno.NOTURNO,
            List.of(new HorarioRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(19, 30),
                LocalTime.of(20, 30)
            ))
        ).id();

        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));

        assertThatThrownBy(() -> matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId)))
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

        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));
        var matricula = matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId));

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

        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaAId));
        var matricula = matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaBId));

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
        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaId));

        assertThatThrownBy(() -> turmaService.vincularProfessor(turmaId, professor.getId()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("professor não pode ser aluno");
    }

    @Test
    void naoPermiteVincularProfessorComConflitoDeHorario() {
        cadastrarSala("sala-professor-conflito-a");
        cadastrarSala("sala-professor-conflito-b");
        Professor professor = cadastrarProfessor(cadastrarMembro("professor-conflito"));
        TurmaResponseDTO turmaA = criarTurmaBasica(
            "sala-professor-conflito-a", "turma professor a", Turma.EnumTipoTurno.NOTURNO
        );
        TurmaResponseDTO turmaB = criarTurmaBasica(
            "sala-professor-conflito-b", "turma professor b", Turma.EnumTipoTurno.NOTURNO
        );
        Aluno alunoA = cadastrarAluno(cadastrarMembro("aluno-professor-conflito-a"));
        Aluno alunoB = cadastrarAluno(cadastrarMembro("aluno-professor-conflito-b"));
        matriculaService.matricular(new MatriculaRequestDTO(alunoA.getId(), turmaA.id()));
        matriculaService.matricular(new MatriculaRequestDTO(alunoB.getId(), turmaB.id()));
        turmaService.vincularProfessor(turmaA.id(), professor.getId());
        configurarPeriodo(turmaA.id(), LocalDate.now().plusDays(7), List.of(horario(DayOfWeek.MONDAY, 19, 20)));
        turmaService.vincularProfessor(turmaB.id(), professor.getId());

        assertThatThrownBy(() -> configurarPeriodo(
            turmaB.id(),
            LocalDate.now().plusDays(7),
            List.of(horario(DayOfWeek.MONDAY, 19, 20))
        ))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("conflito de horários");
    }

    @Test
    void naoPermiteMaisDeUmProfessorAtivoPorTurma() {
        cadastrarSala("sala-limite-professor");
        Long turmaId = criarTurma("sala-limite-professor", "turma limite professor", DayOfWeek.MONDAY, 19, 20).id();
        Professor segundo = cadastrarProfessor(cadastrarMembro("professor-limite-b"));

        assertThatThrownBy(() -> turmaService.vincularProfessor(turmaId, segundo.getId()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("número máximo de professores");
    }

    @Test
    void substituirProfessorEncerraVinculoAnteriorEAtivaONovo() {
        cadastrarSala("sala-substituicao");
        Long turmaId = criarTurmaComInicio(
            "sala-substituicao", "turma substituição", LocalDate.now(), DayOfWeek.MONDAY, 19, 20
        ).id();
        Professor anterior = entityManager.find(
            Professor.class,
            professorTurmaRepository.findFirstByTurma_IdAndStatus(turmaId, "ATIVO").orElseThrow()
                .getProfessor().getId()
        );
        Professor novo = cadastrarProfessor(cadastrarMembro("professor-novo"));

        var novoVinculo = turmaService.substituirProfessor(turmaId, novo.getId(), "motivo");

        assertThat(novoVinculo.idProfessor()).isEqualTo(novo.getId());
        assertThat(novoVinculo.status()).isEqualTo("ATIVO");
        assertThat(novoVinculo.justificativa()).isEqualTo("motivo");
        assertThat(professorTurmaRepository.findFirstByTurma_IdAndStatus(turmaId, "SUBSTITUIDO"))
            .get()
            .extracting(ProfessorTurma::getProfessor)
            .extracting(Professor::getId)
            .isEqualTo(anterior.getId());
    }

    @Test
    void transfereMatriculaERegistraVinculoDeOrigemComoTransferido() {
        cadastrarSala("sala-transferencia-a");
        cadastrarSala("sala-transferencia-b");
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-transferencia"));
        LocalDate inicio = LocalDate.now();
        Long turmaOrigemId = criarTurmaComInicio(
            "sala-transferencia-a", "turma origem", inicio, DayOfWeek.MONDAY, 19, 20
        ).id();
        Long turmaDestinoId = criarTurmaComInicio(
            "sala-transferencia-b", "turma destino", inicio, DayOfWeek.MONDAY, 19, 20
        ).id();
        var matricula = matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaOrigemId));

        var transferida = matriculaService.transferir(
            matricula.id(),
            new TransferirMatriculaRequestDTO(turmaDestinoId, "Solicitação do aluno")
        );

        assertThat(transferida.idTurma()).isEqualTo(turmaDestinoId);
        assertThat(transferida.status()).isEqualTo("ATIVO");
        assertThat(matriculaRepository.findById(matricula.id()).orElseThrow().getStatus()).isEqualTo("TRANSFERIDO");
    }

    @Test
    void naoPermiteAlterarHorarioDeTurmaEmAndamento() {
        cadastrarSala("sala-horario-andamento");
        Long turmaId = criarTurmaComInicio(
            "sala-horario-andamento",
            "turma andamento",
            LocalDate.now(),
            DayOfWeek.MONDAY,
            19,
            20
        ).id();

        assertThatThrownBy(() -> turmaService.atualizar(turmaId, new AtualizarTurmaRequestDTO(
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(horario(DayOfWeek.TUESDAY, 19, 20))
        )))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Não é possível alterar o horário");
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

        Turma.EnumTipoTurno turno = horaInicio >= 18
            ? Turma.EnumTipoTurno.NOTURNO
            : horaInicio >= 13 ? Turma.EnumTipoTurno.VESPERTINO : Turma.EnumTipoTurno.MATUTINO;
        TurmaResponseDTO turma = criarTurmaBasica(codigoSala, nome, turno);
        adicionarParticipantes(turma.id());
        return configurarPeriodo(turma.id(), dataInicio, horarios);
    }

    private TurmaResponseDTO criarTurmaBasica(String codigoSala, String nome, Turma.EnumTipoTurno turno) {
        return turmaService.criar(new CriarTurmaRequestDTO(
            codigoSala,
            10,
            nome,
            turno,
            null
        ));
    }

    private TurmaResponseDTO criarTurmaComInicio(
        String codigoSala,
        String nome,
        LocalDate inicio,
        DayOfWeek dia,
        int horaInicio,
        int horaFim
    ) {
        return criarTurmaComInicio(codigoSala, nome, inicio, dia, horaInicio, horaFim, List.of());
    }

    private TurmaResponseDTO criarTurmaComInicio(
        String codigoSala,
        String nome,
        LocalDate inicio,
        DayOfWeek dia,
        int horaInicio,
        int horaFim,
        List<HorarioRequestDTO> horariosAdicionais
    ) {
        Turma.EnumTipoTurno turno = horaInicio >= 18
            ? Turma.EnumTipoTurno.NOTURNO
            : horaInicio >= 13 ? Turma.EnumTipoTurno.VESPERTINO : Turma.EnumTipoTurno.MATUTINO;
        TurmaResponseDTO turma = criarTurmaBasica(codigoSala, nome, turno);
        adicionarParticipantes(turma.id());
        List<HorarioRequestDTO> horarios = new java.util.ArrayList<>();
        horarios.add(horario(dia, horaInicio, horaFim));
        horarios.addAll(horariosAdicionais);
        return configurarPeriodo(turma.id(), inicio, horarios);
    }

    private TurmaResponseDTO criarTurmaComHorarios(
        String codigoSala,
        String nome,
        LocalDate inicio,
        Turma.EnumTipoTurno turno,
        List<HorarioRequestDTO> horarios
    ) {
        TurmaResponseDTO turma = criarTurmaBasica(codigoSala, nome, turno);
        adicionarParticipantes(turma.id());
        return configurarPeriodo(turma.id(), inicio, horarios);
    }

    private void adicionarParticipantes(Long turmaId) {
        Aluno aluno = cadastrarAluno(cadastrarMembro("aluno-fixture"));
        Professor professor = cadastrarProfessor(cadastrarMembro("professor-fixture"));
        matriculaService.matricular(new MatriculaRequestDTO(aluno.getId(), turmaId));
        turmaService.vincularProfessor(turmaId, professor.getId());
    }

    private TurmaResponseDTO configurarPeriodo(Long turmaId, LocalDate inicio, List<HorarioRequestDTO> horarios) {
        return turmaService.atualizar(turmaId, new AtualizarTurmaRequestDTO(
            null,
            null,
            inicio,
            inicio.plusDays(30),
            null,
            null,
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
