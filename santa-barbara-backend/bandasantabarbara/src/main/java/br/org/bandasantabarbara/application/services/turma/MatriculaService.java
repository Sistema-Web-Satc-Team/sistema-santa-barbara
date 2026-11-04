package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.DesistirMatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaDetalheResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TransferirMatriculaRequestDTO;
import br.org.bandasantabarbara.application.mapper.MatriculaMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.ensino.AlunoRepository;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatriculaService {

    private static final MatriculaAlunoTurma.EnumStatusMatricula STATUS_ATIVO =
        MatriculaAlunoTurma.EnumStatusMatricula.ATIVO;
    private static final ProfessorTurma.EnumStatusTurma STATUS_PROFESSOR_ATIVO =
        ProfessorTurma.EnumStatusTurma.ATIVO;

    private final MatriculaAlunoTurmaRepository matriculaRepository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final PageMapper pageMapper;
    private final MatriculaMapper matriculaMapper;

    public MatriculaService(
        MatriculaAlunoTurmaRepository matriculaRepository,
        TurmaRepository turmaRepository,
        AlunoRepository alunoRepository,
        ProfessorTurmaRepository professorTurmaRepository,
        PageMapper pageMapper,
        MatriculaMapper matriculaMapper
    ) {
        this.matriculaRepository = matriculaRepository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.pageMapper = pageMapper;
        this.matriculaMapper = matriculaMapper;
    }

    @Transactional
    public MatriculaResponseDTO matricular(MatriculaRequestDTO dto) {
        Turma turma = buscarTurmaParaAtualizacao(dto.idTurma());
        podeAdicionarAluno(turma);

        Aluno aluno = alunoRepository.findByIdForUpdate(dto.idAluno())
            .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(turma.getId(), aluno.getId(), STATUS_ATIVO)) {
            throw new ConflictException("O aluno já possui uma matrícula ativa nesta turma.");
        }
        if (professorTurmaRepository.existsByTurma_IdAndProfessor_IdAndStatus(
            turma.getId(), aluno.getId(), STATUS_PROFESSOR_ATIVO
        )) {
            throw new ConflictException("Um professor não pode ser aluno da mesma turma.");
        }
        validarConflitoHorarioAluno(aluno.getId(), turma, null);

        MatriculaAlunoTurma matricula = matriculaRepository.save(
            new MatriculaAlunoTurma(turma, aluno, LocalDate.now())
        );
        return matriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponseDTO> listar(
        Pageable pageable,
        MatriculaAlunoTurma.EnumStatusMatricula status
    ) {
        return pageMapper.toResponse(
            matriculaRepository.listarPorStatus(status, pageable).map(matriculaMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponseDTO> listarDoAluno(
        UUID idAluno,
        Pageable pageable,
        MatriculaAlunoTurma.EnumStatusMatricula status
    ) {
        return pageMapper.toResponse(
            matriculaRepository.listarPorAlunoEStatus(idAluno, status, pageable).map(matriculaMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public MatriculaDetalheResponseDTO buscarPorId(Long idMatricula) {
        MatriculaAlunoTurma matricula = matriculaRepository.buscarDetalhePorId(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula não encontrada."));
        return matriculaMapper.toDetailResponse(matricula);
    }

    @Transactional
    public MatriculaResponseDTO desistir(Long idMatricula, DesistirMatriculaRequestDTO dto) {
        MatriculaAlunoTurma existente = matriculaRepository.findById(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula ativa não encontrada."));
        Turma turma = buscarTurmaParaAtualizacao(existente.getTurma().getId());
        MatriculaAlunoTurma matricula = matriculaRepository.findActiveByIdForUpdate(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula ativa não encontrada."));
        podeDesistir(turma);
        matricula.desistir(LocalDate.now());
        matricula.definirJustificativa(dto == null ? null : dto.justificativa());
        return matriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional
    public void remover(Long idMatricula) {
        MatriculaAlunoTurma existente = matriculaRepository.findById(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula não encontrada."));
        Turma turma = buscarTurmaParaAtualizacao(existente.getTurma().getId());
        MatriculaAlunoTurma matricula = matriculaRepository.findByIdForUpdate(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula não encontrada."));
        podeRemoverAluno(turma);
        if (!matricula.estaAtiva()) {
            throw new ConflictException("Somente uma matrícula ativa pode ser removida.");
        }
        matriculaRepository.delete(matricula);
    }

    @Transactional
    public MatriculaResponseDTO transferir(Long idMatricula, TransferirMatriculaRequestDTO dto) {
        MatriculaAlunoTurma existente = matriculaRepository.findById(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula ativa não encontrada."));
        Long turmaOrigemId = existente.getTurma().getId();
        Turma turmaOrigem;
        Turma turmaDestino;
        if (turmaOrigemId.equals(dto.idNovaTurma())) {
            turmaOrigem = buscarTurmaParaAtualizacao(turmaOrigemId);
            turmaDestino = turmaOrigem;
        } else if (turmaOrigemId < dto.idNovaTurma()) {
            turmaOrigem = buscarTurmaParaAtualizacao(turmaOrigemId);
            turmaDestino = buscarTurmaParaAtualizacao(dto.idNovaTurma());
        } else {
            turmaDestino = buscarTurmaParaAtualizacao(dto.idNovaTurma());
            turmaOrigem = buscarTurmaParaAtualizacao(turmaOrigemId);
        }

        MatriculaAlunoTurma atual = matriculaRepository.findActiveByIdForUpdate(idMatricula)
            .orElseThrow(() -> new NotFoundException("Matrícula ativa não encontrada."));
        podeTransferirAluno(turmaOrigem);
        podeReceberTransferencia(turmaDestino);
        if (turmaOrigem.getId().equals(turmaDestino.getId())) {
            throw new ConflictException("A turma de destino deve ser diferente da turma atual.");
        }
        if (matriculaRepository.existsByTurma_IdAndAluno_IdAndStatus(
            turmaDestino.getId(), atual.getAluno().getId(), STATUS_ATIVO
        )) {
            throw new ConflictException("O aluno já possui uma matrícula ativa na turma de destino.");
        }

        Aluno aluno = alunoRepository.findByIdForUpdate(atual.getAluno().getId())
            .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));
        validarConflitoHorarioAluno(aluno.getId(), turmaDestino, turmaOrigem.getId());

        atual.transferir(LocalDate.now(), dto.justificativa());
        matriculaRepository.save(atual);
        MatriculaAlunoTurma nova = matriculaRepository.save(
            new MatriculaAlunoTurma(turmaDestino, aluno, LocalDate.now())
        );
        nova.definirJustificativa(dto.justificativa());
        return matriculaMapper.toResponse(nova);
    }

    private Turma buscarTurmaParaAtualizacao(Long idTurma) {
        return turmaRepository.findByIdForUpdate(idTurma)
            .orElseThrow(() -> new NotFoundException("Turma não encontrada."));
    }

    private void podeAdicionarAluno(Turma turma) {
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO && turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("A turma não aceita novos alunos.");
        }
        validarVagaDisponivel(turma);
    }

    private void podeRemoverAluno(Turma turma) {
        if (turma.getStatus() != StatusTurma.PLANEJAMENTO) {
            throw new ConflictException("Alunos só podem ser removidos de turmas em planejamento.");
        }
    }

    private void podeTransferirAluno(Turma turma) {
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("Alunos só podem ser transferidos de turmas em andamento.");
        }
    }

    private void podeReceberTransferencia(Turma turma) {
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("A turma de destino da transferência deve estar em andamento.");
        }
        validarVagaDisponivel(turma);
    }

    private void podeDesistir(Turma turma) {
        if (turma.getStatus() != StatusTurma.EM_ANDAMENTO) {
            throw new ConflictException("A desistência só é permitida em turmas em andamento.");
        }
    }

    private void validarVagaDisponivel(Turma turma) {
        if (matriculaRepository.countByTurma_IdAndStatus(turma.getId(), STATUS_ATIVO) >= turma.getNumeroVagas()) {
            throw new ConflictException("A turma já atingiu o limite de vagas.");
        }
    }

    private void validarConflitoHorarioAluno(UUID idAluno, Turma turma, Long turmaOrigemId) {
        if (turma.getHorario() == null || turma.getDataInicio() == null || turma.getDataFim() == null) {
            return;
        }
        if (matriculaRepository.existsScheduleConflict(
            idAluno,
            turma.getId(),
            turmaOrigemId,
            turma.getHorario().getId(),
            turma.getDataInicio(),
            turma.getDataFim()
        )) {
            throw new ConflictException("O aluno já possui uma turma com conflito de horários neste período.");
        }
    }
}
