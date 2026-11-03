package br.org.bandasantabarbara.application.services.aula;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AulaChamadasResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AulaListaResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AulaResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AtualizarChamadasRequest;
import br.org.bandasantabarbara.application.dtos.aulas.ChamadaResponse;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.aula.Aula;
import br.org.bandasantabarbara.model.aula.Chamada;
import br.org.bandasantabarbara.model.turma.HorarioIntervalo;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.aula.AulaRepository;
import br.org.bandasantabarbara.repositories.aula.ChamadaRepository;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AulaService {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final AulaRepository aulaRepository;
    private final ChamadaRepository chamadaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final MatriculaAlunoTurmaRepository matriculaRepository;

    public AulaService(
        AulaRepository aulaRepository,
        ChamadaRepository chamadaRepository,
        TurmaRepository turmaRepository,
        ProfessorTurmaRepository professorTurmaRepository,
        MatriculaAlunoTurmaRepository matriculaRepository
    ) {
        this.aulaRepository = aulaRepository;
        this.chamadaRepository = chamadaRepository;
        this.turmaRepository = turmaRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public AulaResponse iniciar(Long turmaId, UUID membroId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new BadRequestException("Só é possível iniciar uma aula de uma turma em andamento.");
        }

        ProfessorTurma vinculo = obterVinculoAtivo(turmaId, membroId);
        LocalDateTime agora = LocalDateTime.now(ZONE_ID);
        LocalTime horaAtual = agora.toLocalTime();
        DayOfWeek diaAtual = agora.getDayOfWeek();

        if (turma.getHorario() == null) {
            throw new BadRequestException("A turma não possui horários cadastrados.");
        }

        LocalTime fimPrevisto = turma.getHorario().getIntervalos().stream()
            .filter(intervalo -> intervalo.getDiaSemana() == diaAtual)
            .filter(intervalo -> !horaAtual.isBefore(intervalo.getInicio()) && horaAtual.isBefore(intervalo.getFim()))
            .map(HorarioIntervalo::getFim)
            .min(LocalTime::compareTo)
            .orElseThrow(() -> new BadRequestException("O horário atual não pertence a um horário da turma."));

        Aula aula = Aula.iniciar(
            turma,
            vinculo.getProfessor(),
            agora,
            LocalDateTime.of(agora.toLocalDate(), fimPrevisto)
        );
        return new AulaResponse(aulaRepository.save(aula));
    }

    @Transactional
    public AulaResponse finalizar(Long turmaId, Long aulaId, UUID membroId) {
        Aula aula = obterAulaParaEscrita(turmaId, aulaId, membroId);
        LocalDateTime agora = LocalDateTime.now(ZONE_ID);
        if (agora.isAfter(aula.getDataHoraFimPrevista())) {
            throw new BadRequestException("O horário previsto já foi ultrapassado; a aula será encerrada automaticamente.");
        }
        aula.finalizarManualmente(agora);
        return new AulaResponse(aula);
    }

    @Transactional
    public AulaResponse reabrir(Long turmaId, Long aulaId, UUID membroId) {
        Aula aula = obterAulaParaEscrita(turmaId, aulaId, membroId);
        aula.reabrir(LocalDateTime.now(ZONE_ID));
        return new AulaResponse(aula);
    }

    @Transactional
    public List<ChamadaResponse> atualizarChamadas(
        Long turmaId,
        Long aulaId,
        UUID membroId,
        AtualizarChamadasRequest request
    ) {
        Aula aula = obterAulaParaEscrita(turmaId, aulaId, membroId);
        if (aula.isFinalizada()) {
            throw new BadRequestException("Não é possível alterar chamadas de uma aula finalizada.");
        }
        if (LocalDateTime.now(ZONE_ID).isAfter(aula.getDataHoraFimPrevista())) {
            throw new BadRequestException("O horário previsto da aula já foi ultrapassado.");
        }

        
        Set<Long> idsMatriculas = new HashSet<>();
        for (var chamada : request.chamadas()) {
            if (!idsMatriculas.add(chamada.idMatriculaAluno())) {
                throw new BadRequestException("A mesma matrícula não pode aparecer mais de uma vez na chamada.");
            }
        }


        List<MatriculaAlunoTurma> matriculas = matriculaRepository.findAllById(idsMatriculas);
        Map<Long, MatriculaAlunoTurma> matriculasPorId = new HashMap<>();
        for (MatriculaAlunoTurma matricula : matriculas) {
            if (!matricula.getTurma().getId().equals(turmaId)) {
                throw new BadRequestException("Todas as matrículas informadas devem pertencer à turma da aula.");
            }
            matriculasPorId.put(matricula.getId(), matricula);
        }

        if (matriculasPorId.size() != idsMatriculas.size()) {
            throw new BadRequestException("Uma ou mais matrículas informadas não foram encontradas.");
        }

        Set<Long> idsMatriculasDaTurma = matriculaRepository
            .findAllIdsAtivasByTurmaId(turmaId);

        if (!idsMatriculas.equals(idsMatriculasDaTurma)) {
            throw new BadRequestException(
                "A chamada deve conter todos os alunos ativos da turma."
            );
        }

        Map<Long, Chamada> chamadasExistentes = new HashMap<>();
        chamadaRepository.findAllByAula_Id(aulaId).forEach(
            chamada -> chamadasExistentes.put(chamada.getMatriculaAlunoTurma().getId(), chamada)
        );

        List<Chamada> chamadasAtualizadas = new ArrayList<>();
        for (var requestChamada : request.chamadas()) {
            Long matriculaId = requestChamada.idMatriculaAluno();
            Chamada chamada = chamadasExistentes.get(matriculaId);
            if (chamada == null) {
                chamada = new Chamada(aula, matriculasPorId.get(matriculaId), requestChamada.presente());
            } else {
                chamada.atualizarPresenca(requestChamada.presente());
            }
            chamadasAtualizadas.add(chamada);
        }
        return chamadaRepository.saveAll(chamadasAtualizadas).stream()
            .map(ChamadaResponse::new)
            .toList();
    }

    @Transactional(readOnly = true)
    public AulaChamadasResponse buscarChamadas(
        Long turmaId,
        Long aulaId,
        UUID membroId,
        boolean podeConsultarTodas
    ) {
        Aula aula = obterAula(turmaId, aulaId);
        validarConsultaProfessor(turmaId, membroId, podeConsultarTodas);
        List<ChamadaResponse> chamadas = chamadaRepository.listarPorAula(aulaId).stream()
            .map(ChamadaResponse::new)
            .toList();
        return new AulaChamadasResponse(
            aula.getTurma().getNome(),
            aula.getProfessor().getMembro().getNome(),
            contarAlunosAtivos(turmaId),
            new AulaResponse(aula),
            chamadas
        );
    }

    @Transactional(readOnly = true)
    public AulaListaResponse listar(
        Long turmaId,
        UUID membroId,
        boolean podeConsultarTodas,
        Aula.EnumTipoEncerramento tipoEncerramento,
        Pageable pageable
    ) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
        validarConsultaProfessor(turmaId, membroId, podeConsultarTodas);
        Page<Aula> pagina = aulaRepository.listarPorTurmaEEncerramento(turmaId, tipoEncerramento, pageable);
        List<AulaResponse> aulas = pagina.getContent().stream().map(AulaResponse::new).toList();
        String nomeProfessor = professorTurmaRepository
            .findFirstByTurma_IdAndStatus(turmaId, ProfessorTurma.EnumStatusTurma.ATIVO)
            .map(vinculo -> vinculo.getProfessor().getMembro().getNome())
            .orElseGet(() -> pagina.getContent().stream().findFirst()
                .map(aula -> aula.getProfessor().getMembro().getNome())
                .orElse(null));

        return new AulaListaResponse(
            turma.getNome(),
            nomeProfessor,
            contarAlunosAtivos(turmaId),
            new PageResponse<>(
                aulas,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
            )
        );
    }

    @Transactional
    public int encerrarAulasVencidas() {
        return aulaRepository.encerrarAulasVencidas(LocalDateTime.now(ZONE_ID));
    }

    private Aula obterAulaParaEscrita(Long turmaId, Long aulaId, UUID membroId) {
        Aula aula = aulaRepository.findByIdAndTurmaIdForUpdate(aulaId, turmaId)
            .orElseThrow(() -> new NotFoundException("Aula não encontrada para esta turma."));
        ProfessorTurma vinculo = obterVinculoAtivo(turmaId, membroId);
        if (!aula.getProfessor().getId().equals(vinculo.getProfessor().getId())) {
            throw new ForbiddenException("Somente o professor responsável pela aula pode alterá-la.");
        }
        return aula;
    }

    private Aula obterAula(Long turmaId, Long aulaId) {
        return aulaRepository.findByIdAndTurma_Id(aulaId, turmaId)
            .orElseThrow(() -> new NotFoundException("Aula não encontrada para esta turma."));
    }

    private ProfessorTurma obterVinculoAtivo(Long turmaId, UUID membroId) {
        ProfessorTurma vinculo = professorTurmaRepository
            .findActiveByTurmaAndProfessorForUpdate(turmaId, membroId)
            .orElseThrow(() -> new ForbiddenException("O professor não possui vínculo ativo com esta turma."));
        LocalDate hoje = LocalDate.now(ZONE_ID);
        if (vinculo.getDataEntrada().isAfter(hoje)
            || (vinculo.getDataSaida() != null && !vinculo.getDataSaida().isAfter(hoje))) {
            throw new ForbiddenException("O professor não possui vínculo ativo com esta turma.");
        }
        return vinculo;
    }

    // DEBITO-05: Permissão de consulta acoplado ao service.

    private void validarConsultaProfessor(Long turmaId, UUID membroId, boolean podeConsultarTodas) {
        if (!podeConsultarTodas) {
            ProfessorTurma vinculo = professorTurmaRepository
                .findFirstByTurma_IdAndProfessor_IdAndStatus(
                    turmaId,
                    membroId,
                    ProfessorTurma.EnumStatusTurma.ATIVO
                )
                .orElseThrow(() -> new ForbiddenException("O professor não possui vínculo ativo com esta turma."));
            validarPeriodoVinculo(vinculo);
        }
    }

    private void validarPeriodoVinculo(ProfessorTurma vinculo) {
        LocalDate hoje = LocalDate.now(ZONE_ID);
        if (vinculo.getDataEntrada().isAfter(hoje)
            || (vinculo.getDataSaida() != null && !vinculo.getDataSaida().isAfter(hoje))) {
            throw new ForbiddenException("O professor não possui vínculo ativo com esta turma.");
        }
    }

    private long contarAlunosAtivos(Long turmaId) {
        return matriculaRepository.contarMatriculasAtivasPorTurma(
            turmaId,
            MatriculaAlunoTurma.EnumStatusMatricula.ATIVO
        );
    }
}
