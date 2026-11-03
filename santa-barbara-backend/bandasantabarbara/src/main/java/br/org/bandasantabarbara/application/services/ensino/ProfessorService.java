package br.org.bandasantabarbara.application.services.ensino;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorHorarioIntervaloResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorTurmaAtualResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorTurmaHorarioResponse;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.ProfessorMapper;
import br.org.bandasantabarbara.application.mapper.TurmaMapper;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.repositories.turma.AlunoTurmaHorarioProjection;
import br.org.bandasantabarbara.repositories.turma.AlunoTurmaInstrumentoProjection;
import br.org.bandasantabarbara.repositories.turma.AlunoTurmaProjection;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaAlunoProjection;

import br.org.bandasantabarbara.repositories.ensino.ProfessorInstrumentoProjection;
import br.org.bandasantabarbara.repositories.ensino.ProfessorProjection;
import br.org.bandasantabarbara.repositories.ensino.ProfessorRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final ProfessorMapper professorMapper;
    private final PageMapper pageMapper;
    private final TurmaMapper turmaMapper;

    public ProfessorService(
            ProfessorRepository professorRepository,
            ProfessorTurmaRepository professorTurmaRepository,
            ProfessorMapper professorMapper,
            PageMapper pageMapper,
            TurmaMapper turmaMapper
    ) {
        this.professorRepository = professorRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.professorMapper = professorMapper;
        this.pageMapper = pageMapper;
        this.turmaMapper = turmaMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfessorResponse> listar(
            Pageable pageable,
            String search,
            List<String> instrumentosEnsinando
    ) {
        List<String> instrumentosFiltrados = instrumentosEnsinando == null
                ? List.of()
                : instrumentosEnsinando.stream()
                        .filter(instrumento -> instrumento != null && !instrumento.isBlank())
                        .map(instrumento -> instrumento.trim().toLowerCase())
                        .distinct()
                        .toList();

        String busca = search == null || search.isBlank() ? null : search.trim();
        boolean filtrarInstrumentos = !instrumentosFiltrados.isEmpty();
        List<String> parametrosInstrumentos = filtrarInstrumentos
                ? instrumentosFiltrados
                : List.of("");

        var professores = professorRepository.buscarProfessores(
                busca,
                filtrarInstrumentos,
                parametrosInstrumentos,
                pageable
        );

        List<UUID> professorIds = professores.getContent()
                .stream()
                .map(ProfessorProjection::getId)
                .toList();

        if (professorIds.isEmpty()) {
            return pageMapper.toResponse(professores.map(professor ->
                    professorMapper.toResponse(professor, false, List.of())
            ));
        }

        // Isso serve para evitar o famoso problema N+1,
        // onde para cada professor, uma query é executada para buscar os instrumentos ensinados e

        Set<UUID> idsComTurma = Set.copyOf(
                professorTurmaRepository.findProfessorIdsComTurmaAtiva(professorIds)
        );

        Map<UUID, List<String>> instrumentosPorProfessor = professorTurmaRepository
                .findInstrumentosEnsinados(professorIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ProfessorInstrumentoProjection::getProfessorId,
                        Collectors.mapping(
                                ProfessorInstrumentoProjection::getInstrumento,
                                Collectors.toList()
                        )
                ));

        var resultado = professores.map(professor -> professorMapper.toResponse(
                professor,
                idsComTurma.contains(professor.getId()),
                instrumentosPorProfessor.getOrDefault(professor.getId(), List.of())
        ));

        return pageMapper.toResponse(resultado);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfessorTurmaAtualResponse> listarTurmasAtuais(UUID idProfessor, Pageable pageable) {
        List<AlunoTurmaProjection> turmas = professorTurmaRepository.listarTurmasAtuaisDoProfessor(
                idProfessor,
                ProfessorTurma.EnumStatusTurma.ATIVO
        );
        if (turmas.isEmpty()) {
            return pageMapper.toResponse(new PageImpl<>(List.of(), pageable, 0));
        }

        List<Long> turmaIds = turmas.stream().map(AlunoTurmaProjection::getTurmaId).toList();
        Map<Long, List<String>> alunosPorTurma =
                professorTurmaRepository.listarAlunosAtivosPorTurmas(
                                turmaIds,
                                MatriculaAlunoTurma.EnumStatusMatricula.ATIVO
                        )
                        .stream()
                        .collect(Collectors.groupingBy(
                                ProfessorTurmaAlunoProjection::getTurmaId,
                                Collectors.mapping(ProfessorTurmaAlunoProjection::getAluno, Collectors.toList())
                        ));
        Map<Long, List<String>> instrumentosPorTurma =
                professorTurmaRepository.listarInstrumentosPorTurmas(turmaIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                AlunoTurmaInstrumentoProjection::getTurmaId,
                                Collectors.mapping(
                                        AlunoTurmaInstrumentoProjection::getInstrumento,
                                        Collectors.toList()
                                )
                        ));

        List<ProfessorTurmaAtualResponse> respostas = turmas.stream()
                .map(turma -> new ProfessorTurmaAtualResponse(
                        turma.getTurma(),
                        turma.getProfessor(),
                        turma.getSala(),
                        turma.getTurno(),
                        alunosPorTurma.getOrDefault(turma.getTurmaId(), List.of()),
                        instrumentosPorTurma.getOrDefault(turma.getTurmaId(), List.of())
                ))
                .toList();
        return paginar(respostas, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfessorTurmaHorarioResponse> listarHorariosAtuais(
            UUID idProfessor,
            Pageable pageable
    ) {
        List<AlunoTurmaProjection> turmas = professorTurmaRepository.listarTurmasAtuaisDoProfessor(
                idProfessor,
                ProfessorTurma.EnumStatusTurma.ATIVO
        );
        if (turmas.isEmpty()) {
            return pageMapper.toResponse(new PageImpl<>(List.of(), pageable, 0));
        }

        Map<Long, List<ProfessorHorarioIntervaloResponse>> horariosPorTurma =
                professorTurmaRepository.listarHorariosAtuaisDoProfessor(
                                idProfessor,
                                ProfessorTurma.EnumStatusTurma.ATIVO
                        )
                        .stream()
                        .collect(Collectors.groupingBy(
                                AlunoTurmaHorarioProjection::getTurmaId,
                                Collectors.mapping(
                                        intervalo -> new ProfessorHorarioIntervaloResponse(
                                                nomeDiaDaSemana(intervalo.getDiaDaSemana()),
                                                intervalo.getInicio(),
                                                intervalo.getFim()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        List<ProfessorTurmaHorarioResponse> respostas = turmas.stream()
                .map(turma -> new ProfessorTurmaHorarioResponse(
                        turma.getTurma(),
                        turma.getProfessor(),
                        turma.getSala(),
                        turma.getTurno(),
                        horariosPorTurma.getOrDefault(turma.getTurmaId(), List.of())
                ))
                .toList();
        return paginar(respostas, pageable);
    }

    private <T> PageResponse<T> paginar(List<T> itens, Pageable pageable) {
        long offset = pageable.getOffset();
        if (offset >= itens.size()) {
            return pageMapper.toResponse(new PageImpl<>(List.of(), pageable, itens.size()));
        }
        int inicio = Math.toIntExact(offset);
        int fim = (int) Math.min(offset + pageable.getPageSize(), itens.size());
        return pageMapper.toResponse(new PageImpl<>(itens.subList(inicio, fim), pageable, itens.size()));
    }

    private String nomeDiaDaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "SEGUNDA";
            case TUESDAY -> "TERCA";
            case WEDNESDAY -> "QUARTA";
            case THURSDAY -> "QUINTA";
            case FRIDAY -> "SEXTA";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

}
