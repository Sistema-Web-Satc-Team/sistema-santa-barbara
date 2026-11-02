package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorResponse;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.GradeTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaDetalheResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaAlunoResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaProfessorResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseResumidoDTO;
import br.org.bandasantabarbara.application.mapper.MatriculaMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.ProfessorMapper;
import br.org.bandasantabarbara.application.mapper.ProfessorTurmaMapper;
import br.org.bandasantabarbara.application.mapper.TurmaMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.musica.Instrumento;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.ensino.ProfessorRepository;
import br.org.bandasantabarbara.repositories.musica.InstrumentoRepository;
import br.org.bandasantabarbara.repositories.turma.ContagemPorTurma;
import br.org.bandasantabarbara.repositories.turma.InstrumentoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    private static final MatriculaAlunoTurma.EnumStatusMatricula STATUS_MATRICULA_ATIVA =
        MatriculaAlunoTurma.EnumStatusMatricula.ATIVO;
    private static final ProfessorTurma.EnumStatusTurma STATUS_PROFESSOR_ATIVO =
        ProfessorTurma.EnumStatusTurma.ATIVO;

    private final TurmaRepository turmaRepository;
    private final SalaService salaService;
    private final TurmaMapper turmaMapper;
    private final PageMapper pageMapper;
    private final ProfessorRepository professorRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final MatriculaAlunoTurmaRepository matriculaRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final InstrumentoTurmaRepository instrumentoTurmaRepository;
    private final ProfessorTurmaMapper professorTurmaMapper;

    private final MatriculaMapper matriculaMapper;

    public TurmaService(
        TurmaRepository turmaRepository,
        SalaService salaService,
        TurmaMapper turmaMapper,
        PageMapper pageMapper,
        ProfessorRepository professorRepository,
        InstrumentoRepository instrumentoRepository,
        MatriculaAlunoTurmaRepository matriculaRepository,
        ProfessorTurmaRepository professorTurmaRepository,
        InstrumentoTurmaRepository instrumentoTurmaRepository,
        ProfessorTurmaMapper professorTurmaMapper,
        MatriculaMapper matriculaMapper

    ) {
        this.turmaRepository = turmaRepository;
        this.salaService = salaService;
        this.turmaMapper = turmaMapper;
        this.pageMapper = pageMapper;
        this.professorRepository = professorRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.instrumentoTurmaRepository = instrumentoTurmaRepository;
        this.professorTurmaMapper = professorTurmaMapper;
        this.matriculaMapper = matriculaMapper;
    }

    @Transactional
    public TurmaResponseDTO criar(CriarTurmaRequestDTO dto) {
        Sala sala = salaService.buscarEntidadePorCodigoParaAgendamento(dto.codigoSala());
        validarCapacidade(dto.numeroVagas(), sala);

        List<Instrumento> instrumentos = buscarInstrumentos(dto.idsInstrumentos());
        Turma turma = turmaMapper.toEntity(dto, sala);
        Turma turmaSalva = turmaRepository.save(turma);
        turmaSalva.definirInstrumentos(instrumentos);
        instrumentoTurmaRepository.saveAll(turmaSalva.getInstrumentos());

        return turmaMapper.toResponse(turmaSalva);
    }

    @Transactional
    public TurmaResponseDTO atualizar(Long id, AtualizarTurmaRequestDTO dto) {
        Turma turma = buscarTurmaParaAtualizacao(id);
        validarTurmaPodeSerAlterada(turma);
        boolean alterandoHorario = dto.horarios() != null || dto.turno() != null;
        boolean definindoPeriodo = dto.dataInicio() != null || dto.dataFim() != null;
        if (alterandoHorario) {
            validarPodeAlterarHorario(turma);
        }
        String codigoSala = dto.codigoSala() != null ? dto.codigoSala() : turma.getSala().getCode();
        Sala sala = salaService.buscarEntidadePorCodigoParaAgendamento(codigoSala);

        Integer vagas = dto.numeroVagas() != null ? dto.numeroVagas() : turma.getNumeroVagas();
        validarCapacidade(vagas, sala);

        long matriculasAtivas = matriculaRepository.countByTurma_IdAndStatus(id, STATUS_MATRICULA_ATIVA);
        if (vagas < matriculasAtivas) {
            throw new ConflictException("O número de vagas não pode ser menor que as matrículas ativas.");
        }

        if (turma.getStatus() == StatusTurma.EM_ANDAMENTO && dto.dataInicio() != null) {
            throw new ConflictException("A data de início não pode ser alterada após o início da turma.");
        }

        turmaMapper.toUpdate(dto, turma, sala);
        if (definindoPeriodo) {
            validarPeriodo(turma.getDataInicio(), turma.getDataFim());
            validarPeriodoConfigurado(turma);
        }
        /*if (turma.getDataInicio() != null && turma.getDataFim() != null) {
            validarConflitoSala(sala, turma.getDataInicio(), turma.getDataFim(), id);
        }*/
        turmaRepository.saveAndFlush(turma);

        if ((alterandoHorario || definindoPeriodo)
            && turma.getHorario() != null
            && turma.getDataInicio() != null
            && turma.getDataFim() != null) {
            if (matriculaRepository.existsScheduleConflictForTurma(
                id,
                turma.getHorario().getId(),
                turma.getDataInicio(),
                turma.getDataFim()
            )) {
                throw new ConflictException("A alteração cria conflito de horários para alunos já matriculados.");
            }
            if (professorTurmaRepository.existsScheduleConflictForTurma(
                id,
                turma.getHorario().getId(),
                turma.getDataInicio(),
                turma.getDataFim()
            )) {
                throw new ConflictException("A alteração cria conflito de horários para professores já alocados.");
            }
        }

        if (dto.idsInstrumentos() != null) {
            List<Instrumento> instrumentos = buscarInstrumentos(dto.idsInstrumentos());
            instrumentoTurmaRepository.excluirPorTurma(id);
            turma.definirInstrumentos(instrumentos);
            instrumentoTurmaRepository.saveAll(turma.getInstrumentos());
        }

        return turmaMapper.toResponse(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
    public TurmaResponseDTO buscarPorId(Long id) {
        Turma turma = turmaRepository.findDetailsById(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));

        turmaRepository.findWithMatriculasById(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
        turmaRepository.findWithProfessoresById(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));

        turma = turmaRepository.findWithInstrumentosById(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));

        return turmaMapper.toResponse(turma);
    }

    public List<MatriculaDetalheResponseDTO> buscarAlunosTurmaPorId(Long idTurma, MatriculaAlunoTurma.EnumStatusMatricula status) {
        Turma turma = buscarTurmaPorId(idTurma);
        return turma.getMatriculas().stream()
            .filter(matricula -> status == null || matricula.getStatus() == status)
            .map(matriculaMapper::toDetailResponse)
            .toList();
    }

    public List<ProfessorTurmaResponseDTO> buscarProfessoresTurmaPorId(Long idTurma, ProfessorTurma.EnumStatusTurma status) {
        Turma turma = buscarTurmaPorId(idTurma);
        return turma.getProfessores().stream()
            .filter(professorTurma -> status == null || professorTurma.getStatus() == status)
            .map(professorTurmaMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponseResumidoDTO> listar(Pageable pageable) {
        return listar(pageable, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponseResumidoDTO> listar(Pageable pageable, StatusTurma status) {
        Page<Turma> turmas = turmaRepository.findAllWithSalaAndOptionalFilters(status, pageable);

        Set<Long> idTurmas =  turmas.stream().map(Turma::getId).collect(Collectors.toSet());

        Map<Long, Long> numeroDeProfessoresAtivos =
            professorTurmaRepository.contarProfessoresAtivosPorTurmas(idTurmas)
                .stream()
                .collect(Collectors.toMap(
                    ContagemPorTurma::getIdTurma,
                    ContagemPorTurma::getQuantidade
                ));

        Map<Long, Long> numeroDeAlunosAtivos =
            matriculaRepository.contarMatriculasAtivasPorTurmas(idTurmas)
                .stream()
                .collect(Collectors.toMap(
                    ContagemPorTurma::getIdTurma,
                    ContagemPorTurma::getQuantidade
                ));

        turmas.forEach(turma -> {
            turma.setNumeroDeProfessoresAtivos(numeroDeProfessoresAtivos.getOrDefault(turma.getId(), 0L));
            turma.setNumeroDeAlunosAtivos(numeroDeAlunosAtivos.getOrDefault(turma.getId(), 0L));
        });

        return pageMapper.toResponse(turmas.map(turmaMapper::toResponseResumido));
    }

    @Transactional(readOnly = true)
    public PageResponse<GradeTurmaResponseDTO> listarGradeHorarios(Pageable pageable) {
        Page<Turma> turmas = turmaRepository.findAllWithHorarioSalaAndOptionalFilters(
            StatusTurma.PLANEJAMENTO,
            pageable
        );
        return pageMapper.toResponse(turmas.map(turmaMapper::toGradeResponse));
    }

    @Transactional
    public ProfessorTurmaResponseDTO vincularProfessor(Long idTurma, UUID idProfessor) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        podeAdicionarProfessor(turma);

        Professor professor = professorRepository.findByIdForUpdate(idProfessor)
            .orElseThrow(() -> new NotFoundException("Professor não encontrado."));

        if (professorTurmaRepository.existsByTurma_IdAndProfessor_IdAndStatus(
            idTurma, idProfessor, STATUS_PROFESSOR_ATIVO
        )) {
            throw new ConflictException("O professor já possui um vínculo ativo com esta turma.");
        }

        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(
            idTurma, idProfessor, STATUS_MATRICULA_ATIVA
        )) {
            throw new ConflictException("Um professor não pode ser aluno da mesma turma.");
        }
        if (professorTurmaRepository.countByTurma_IdAndStatus(idTurma, STATUS_PROFESSOR_ATIVO) >= 1) {
            throw new ConflictException("A turma já possui o número máximo de professores permitidos.");
        }
        validarConflitoHorarioProfessor(idProfessor, turma);

        ProfessorTurma vinculo = professorTurmaRepository.save(
            new ProfessorTurma(turma, professor, LocalDate.now())
        );

        return professorTurmaMapper.toResponse(vinculo);
    }

    @Transactional
    public void removerProfessor(Long idTurma, UUID idProfessor) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        podeRemoverProfessor(turma);
        ProfessorTurma vinculo = professorTurmaRepository.findActiveByTurmaAndProfessorForUpdate(idTurma, idProfessor)
            .orElseThrow(() -> new NotFoundException("Vínculo ativo do professor não encontrado."));
        professorTurmaRepository.delete(vinculo);
    }

    @Transactional
    public ProfessorTurmaResponseDTO substituirProfessor(Long idTurma, UUID idNovoProfessor, String justificativa) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        podeSubstituirProfessor(turma);
        ProfessorTurma vinculoAtual = professorTurmaRepository.findFirstByTurma_IdAndStatus(
            idTurma,
            STATUS_PROFESSOR_ATIVO
        )
            .orElseThrow(() -> new ConflictException("Não há professor ativo para substituir nesta turma."));
        if (vinculoAtual.getProfessor().getId().equals(idNovoProfessor)) {
            throw new ConflictException("O novo professor deve ser diferente do professor atual.");
        }
        Professor novoProfessor = professorRepository.findByIdForUpdate(idNovoProfessor)
            .orElseThrow(() -> new NotFoundException("Professor não encontrado."));
        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(
            idTurma,
            idNovoProfessor,
            STATUS_MATRICULA_ATIVA
        )) {
            throw new ConflictException("Um professor não pode ser aluno da mesma turma.");
        }
        if (professorTurmaRepository.existsByTurma_IdAndProfessor_IdAndStatus(
            idTurma,
            idNovoProfessor,
            STATUS_PROFESSOR_ATIVO
        )) {
            throw new ConflictException("O novo professor já possui um vínculo ativo com esta turma.");
        }
        validarConflitoHorarioProfessor(idNovoProfessor, turma);

        vinculoAtual.substituir(LocalDate.now());
        vinculoAtual.setJustificativa(justificativa);
        professorTurmaRepository.save(vinculoAtual);
        ProfessorTurma novoVinculo = professorTurmaRepository.save(
            new ProfessorTurma(turma, novoProfessor, LocalDate.now())
        );
        return professorTurmaMapper.toResponse(novoVinculo);
    }

    @Transactional
    public TurmaResponseDTO encerrar(Long idTurma) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("Somente uma turma em andamento pode ser encerrada.");
        }
        return encerrarVinculos(turma);
    }

    @Transactional
    public TurmaResponseDTO cancelar(Long idTurma) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO) {
            throw new ConflictException("Somente uma turma em planejamento pode ser cancelada.");
        }
        return encerrarVinculos(turma);
    }

    @Transactional
    public TurmaResponseDTO interromper(Long idTurma) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("Somente uma turma em andamento pode ser interrompida.");
        }
        return encerrarVinculos(turma);
    }

    @Transactional(readOnly = true)
    public Turma buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
    }

    private Turma buscarTurmaParaAtualizacao(Long id) {
        return turmaRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
    }

    private void validarCapacidade(Integer numeroVagas, Sala sala) {
        if (numeroVagas == null || numeroVagas <= 0) {
            throw new BadRequestException("O número de vagas deve ser maior que zero.");
        }
        if (numeroVagas > sala.getCapacidade()) {
            throw new BadRequestException(
                "O número de vagas não pode exceder a capacidade máxima da sala "
                    + sala.getCode() + ": " + sala.getCapacidade()
            );
        }
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new BadRequestException("A data de início e a data de fim planejada são obrigatórias.");
        }
        if (inicio.isAfter(fim)) {
            throw new BadRequestException("A data de fim deve ocorrer após a data de início.");
        }
    }

    private void validarPeriodoConfigurado(Turma turma) {
        if (turma.getHorario() == null || turma.getHorario().getIntervalos().isEmpty()) {
            throw new BadRequestException("Defina ao menos um horário antes de configurar o período da turma.");
        }
        if (matriculaRepository.countByTurma_IdAndStatus(turma.getId(), STATUS_MATRICULA_ATIVA) == 0) {
            throw new BadRequestException("Matricule ao menos um aluno antes de configurar o período da turma.");
        }
        if (professorTurmaRepository.countByTurma_IdAndStatus(turma.getId(), STATUS_PROFESSOR_ATIVO) == 0) {
            throw new BadRequestException("Vincule ao menos um professor antes de configurar o período da turma.");
        }
    }

    /*private void validarConflitoSala(Sala sala, LocalDate inicio, LocalDate fim, Long turmaId) {
        if (turmaRepository.existsRoomConflict(sala.getId(), inicio, fim, turmaId)) {
            throw new ConflictException("A sala já está alocada a outra turma nesse período.");
        }
    }*/

    private List<Instrumento> buscarInstrumentos(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        if (new LinkedHashSet<>(ids).size() != ids.size()) {
            throw new BadRequestException("A lista de instrumentos não pode conter IDs repetidos.");
        }
        if (ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new BadRequestException("Os IDs dos instrumentos devem ser positivos.");
        }

        List<Instrumento> instrumentos = instrumentoRepository.findAllById(ids);
        if (instrumentos.size() != ids.size()) {
            throw new NotFoundException("Um ou mais instrumentos informados não foram encontrados.");
        }
        return instrumentos;
    }

    private TurmaResponseDTO encerrarVinculos(Turma turma) {
        turma.terminar();
        LocalDate dataSaida = turma.getDataTermino();
        turmaRepository.saveAndFlush(turma);
        matriculaRepository.encerrarMatriculasAtivas(turma.getId(), dataSaida);
        professorTurmaRepository.encerrarVinculosAtivos(turma.getId(), dataSaida);
        return turmaMapper.toResponse(turma);
    }

    private void validarTurmaPodeSerAlterada(Turma turma) {
        if (turma.getStatus() == StatusTurma.CANCELADO
            || turma.getStatus() == StatusTurma.INTERROMPIDA
            || turma.getStatus() == StatusTurma.ENCERRADO) {
            throw new ConflictException("Não é possível alterar uma turma cancelada, interrompida ou encerrada.");
        }
    }

    private void validarPodeAlterarHorario(Turma turma) {
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO) {
            throw new ConflictException("Não é possível alterar o horário de uma turma que já iniciou ou foi encerrada.");
        }
    }

    private void podeAdicionarProfessor(Turma turma) {
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO && turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("A turma não aceita novos professores.");
        }
    }

    private void podeRemoverProfessor(Turma turma) {
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO) {
            throw new ConflictException("Professores só podem ser removidos de turmas em planejamento.");
        }
    }

    private void podeSubstituirProfessor(Turma turma) {
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("Professores só podem ser substituídos em turmas em andamento.");
        }
    }

    private void validarConflitoHorarioProfessor(UUID idProfessor, Turma turma) {
        if (turma.getHorario() == null || turma.getDataInicio() == null || turma.getDataFim() == null) {
            return;
        }
        if (professorTurmaRepository.existsScheduleConflict(
            idProfessor,
            turma.getId(),
            turma.getHorario().getId(),
            turma.getDataInicio(),
            turma.getDataFim()
        )) {
            throw new ConflictException("O professor já possui uma turma com conflito de horários neste período.");
        }
    }
}
