package br.org.bandasantabarbara.application.services.ensino;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorResponse;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.ProfessorMapper;
import br.org.bandasantabarbara.repositories.ensino.ProfessorInstrumentoProjection;
import br.org.bandasantabarbara.repositories.ensino.ProfessorProjection;
import br.org.bandasantabarbara.repositories.ensino.ProfessorRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;
    private final ProfessorMapper professorMapper;
    private final PageMapper pageMapper;

    public ProfessorService(
            ProfessorRepository professorRepository,
            ProfessorTurmaRepository professorTurmaRepository,
            ProfessorMapper professorMapper,
            PageMapper pageMapper
    ) {
        this.professorRepository = professorRepository;
        this.professorTurmaRepository = professorTurmaRepository;
        this.professorMapper = professorMapper;
        this.pageMapper = pageMapper;
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
}
