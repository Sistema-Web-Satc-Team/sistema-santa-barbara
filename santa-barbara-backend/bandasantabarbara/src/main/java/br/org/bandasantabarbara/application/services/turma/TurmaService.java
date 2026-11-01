package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.TurmaMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.musica.Instrumento;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.ensino.AlunoRepository;
import br.org.bandasantabarbara.repositories.ensino.ProfessorRepository;
import br.org.bandasantabarbara.repositories.musica.InstrumentoRepository;
import br.org.bandasantabarbara.repositories.turma.InstrumentoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    private static final String STATUS_ATIVO = "ATIVO";

    private final TurmaRepository turmaRepository;
    private final SalaService salaService;
    private final TurmaMapper turmaMapper;
    private final PageMapper pageMapper;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final MatriculaAlunoTurmaRepository matriculaRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final InstrumentoTurmaRepository instrumentoTurmaRepository;

    public TurmaService(
        TurmaRepository turmaRepository,
        SalaService salaService,
        TurmaMapper turmaMapper,
        PageMapper pageMapper,
        AlunoRepository alunoRepository,
        ProfessorRepository professorRepository,
        InstrumentoRepository instrumentoRepository,
        MatriculaAlunoTurmaRepository matriculaRepository,
        ProfessorTurmaRepository professorTurmaRepository,
        InstrumentoTurmaRepository instrumentoTurmaRepository
    ) {
        this.turmaRepository = turmaRepository;
        this.salaService = salaService;
        this.turmaMapper = turmaMapper;
        this.pageMapper = pageMapper;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.instrumentoTurmaRepository = instrumentoTurmaRepository;
    }

    @Transactional
    public TurmaResponseDTO criar(CriarTurmaRequestDTO dto) {
        Sala sala = salaService.buscarEntidadePorCodigoParaAgendamento(dto.codigoSala());
        validarCapacidade(dto.numeroVagas(), sala);
        validarPeriodo(dto.dataInicio(), dto.dataFim());
        validarConflitoSala(sala, dto.dataInicio(), dto.dataFim(), null);

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
        if (turma.getDataTermino() != null) {
            throw new ConflictException("Não é possível alterar uma turma encerrada.");
        }
        String codigoSala = dto.codigoSala() != null ? dto.codigoSala() : turma.getSala().getCode();
        Sala sala = salaService.buscarEntidadePorCodigoParaAgendamento(codigoSala);

        Integer vagas = dto.numeroVagas() != null ? dto.numeroVagas() : turma.getNumeroVagas();
        validarCapacidade(vagas, sala);

        long matriculasAtivas = matriculaRepository.countByTurma_IdAndStatus(id, STATUS_ATIVO);
        if (vagas < matriculasAtivas) {
            throw new ConflictException("O número de vagas não pode ser menor que as matrículas ativas.");
        }

        alunoRepository.findActiveStudentsByTurmaForUpdate(id);
        LocalDate dataInicio = dto.dataInicio() != null ? dto.dataInicio() : turma.getDataInicio();
        LocalDate dataFim = dto.dataFim() != null ? dto.dataFim() : turma.getDataFim();
        validarPeriodo(dataInicio, dataFim);
        validarConflitoSala(sala, dataInicio, dataFim, id);

        turmaMapper.toUpdate(dto, turma, sala);
        turmaRepository.saveAndFlush(turma);

        if (matriculaRepository.existsScheduleConflictForTurma(
            id,
            turma.getHorario().getId(),
            turma.getDataInicio(),
            turma.getDataFim()
        )) {
            throw new ConflictException("A alteração cria conflito de horários para alunos já matriculados.");
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
        return turmaMapper.toResponse(turma);
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponseDTO> listar(Pageable pageable) {
        return listar(pageable, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponseDTO> listar(Pageable pageable, StatusTurma status) {
        Page<Turma> turmas = turmaRepository.findAllWithHorarioSalaAndOptionalFilters(status, pageable);
        return pageMapper.toResponse(turmas.map(turmaMapper::toResponse));
    }

    @Transactional
    public MatriculaResponseDTO matricular(MatriculaRequestDTO dto) {
        Turma turma = buscarTurmaParaAtualizacao(dto.idTurma());
        validarTurmaAceitaVinculos(turma);

        Aluno aluno = alunoRepository.findByIdForUpdate(dto.idAluno())
            .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(turma.getId(), aluno.getId(), STATUS_ATIVO)) {
            throw new ConflictException("O aluno já possui uma matrícula ativa nesta turma.");
        }

        if (professorTurmaRepository.existsByTurma_IdAndProfessor_IdAndStatus(
            turma.getId(), aluno.getId(), STATUS_ATIVO
        )) {
            throw new ConflictException("Um professor não pode ser aluno da mesma turma.");
        }

        if (matriculaRepository.countByTurma_IdAndStatus(turma.getId(), STATUS_ATIVO) >= turma.getNumeroVagas()) {
            throw new ConflictException("A turma já atingiu o limite de vagas.");
        }

        if (matriculaRepository.existsScheduleConflict(
            aluno.getId(),
            turma.getId(),
            turma.getHorario().getId(),
            turma.getDataInicio(),
            turma.getDataFim()
        )) {
            throw new ConflictException("O aluno já possui uma turma com conflito de horários neste período.");
        }

        MatriculaAlunoTurma matricula = matriculaRepository.save(
            new MatriculaAlunoTurma(turma, aluno, LocalDate.now())
        );
        return toResponse(matricula);
    }

    @Transactional
    public MatriculaResponseDTO desistirMatricula(Long idMatricula) {
        MatriculaAlunoTurma matricula = matriculaRepository.findActiveByIdForUpdate(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula ativa não encontrada."));
        matricula.desistir(LocalDate.now());
        return toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public ProfessorTurmaResponseDTO vincularProfessor(Long idTurma, UUID idProfessor) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        validarTurmaAceitaVinculos(turma);

        Professor professor = professorRepository.findById(idProfessor)
            .orElseThrow(() -> new NotFoundException("Professor não encontrado."));

        if (professorTurmaRepository.existsByTurma_IdAndProfessor_IdAndStatus(
            idTurma, idProfessor, STATUS_ATIVO
        )) {
            throw new ConflictException("O professor já possui um vínculo ativo com esta turma.");
        }

        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(
            idTurma, idProfessor, STATUS_ATIVO
        )) {
            throw new ConflictException("Um professor não pode ser aluno da mesma turma.");
        }

        ProfessorTurma vinculo = professorTurmaRepository.save(
            new ProfessorTurma(turma, professor, LocalDate.now())
        );

        return new ProfessorTurmaResponseDTO(
            vinculo.getId(),
            professor.getId(),
            turma.getId(),
            vinculo.getStatus(),
            vinculo.getDataEntrada(),
            vinculo.getDataSaida()
        );
    }

    @Transactional
    public TurmaResponseDTO encerrar(Long idTurma) {
        Turma turma = buscarTurmaParaAtualizacao(idTurma);
        if (turma.getDataTermino() != null) {
            throw new ConflictException("A turma já foi encerrada.");
        }

        turma.terminar();
        LocalDate dataSaida = turma.getDataTermino();
        matriculaRepository.encerrarMatriculasAtivas(idTurma, dataSaida);
        professorTurmaRepository.encerrarVinculosAtivos(idTurma, dataSaida);

        return turmaMapper.toResponse(turmaRepository.save(turma));
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

    private void validarConflitoSala(Sala sala, LocalDate inicio, LocalDate fim, Long turmaId) {
        if (turmaRepository.existsRoomConflict(sala.getId(), inicio, fim, turmaId)) {
            throw new ConflictException("A sala já está alocada a outra turma nesse período.");
        }
    }

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

    private void validarTurmaAceitaVinculos(Turma turma) {
        if (!turma.permiteNovasMatriculas()) {
            throw new ConflictException("A turma não aceita novos vínculos.");
        }
    }

    private MatriculaResponseDTO toResponse(MatriculaAlunoTurma matricula) {
        return new MatriculaResponseDTO(
            matricula.getId(),
            matricula.getAluno().getId(),
            matricula.getTurma().getId(),
            matricula.getStatus(),
            matricula.getDataMatricula(),
            matricula.getDataSaida()
        );
    }
}
