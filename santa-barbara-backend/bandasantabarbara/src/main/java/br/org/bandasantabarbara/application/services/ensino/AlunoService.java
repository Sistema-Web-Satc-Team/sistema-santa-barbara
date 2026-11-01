package br.org.bandasantabarbara.application.services.ensino;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.mapper.AlunoMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import br.org.bandasantabarbara.repositories.ensino.AlunoInstrumentoProjection;
import br.org.bandasantabarbara.repositories.ensino.AlunoProjection;
import br.org.bandasantabarbara.repositories.ensino.AlunoRepository;
import br.org.bandasantabarbara.repositories.membro.MembroResponsavelRepository;
import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;


@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final MatriculaAlunoTurmaRepository matriculaAlunoTurmaRepository;
    private final AlunoMapper alunoMapper;
    private final PageMapper pageMapper;

    public AlunoService(
            AlunoRepository alunoRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            MatriculaAlunoTurmaRepository matriculaAlunoTurmaRepository,
            AlunoMapper alunoMapper,
            PageMapper pageMapper
    ) {
        this.alunoRepository = alunoRepository;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.matriculaAlunoTurmaRepository = matriculaAlunoTurmaRepository;
        this.alunoMapper = alunoMapper;
        this.pageMapper = pageMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<AlunoResponse> listar(
            Pageable pageable,
            String search,
            List<String> instrumentosAprendendo,
            boolean apenasMenores
    ) {
        List<String> instrumentosFiltrados = instrumentosAprendendo == null
                ? List.of()
                : instrumentosAprendendo.stream()
                        .filter(instrumento -> instrumento != null && !instrumento.isBlank())
                        .map(instrumento -> instrumento.trim().toLowerCase())
                        .distinct()
                        .toList();

        String busca = search == null || search.isBlank() ? null : search.trim();
        boolean filtrarInstrumentos = !instrumentosFiltrados.isEmpty();
        List<String> parametrosInstrumentos = filtrarInstrumentos
                ? instrumentosFiltrados
                : List.of("");

        var alunos = alunoRepository.buscarAlunos(
                busca,
                filtrarInstrumentos,
                parametrosInstrumentos,
                apenasMenores,
                pageable
        );

        List<UUID> alunosIds = alunos.getContent()
                .stream()
                .map(AlunoProjection::getId)
                .toList();

        if (alunosIds.isEmpty()) {
            return pageMapper.toResponse(alunos.map(aluno ->
                    alunoMapper.toResponse(aluno, false, List.of(), List.of())
            ));
        }


        // Isso serve para evitar o famoso problema N+1, 
        // onde para cada aluno, uma query é executada para buscar os instrumentos e responsáveis.

        List<UUID> idsComTurma = this.matriculaAlunoTurmaRepository
                .findAlunoIdsComMatriculaAtiva(alunosIds);
        
        Set<UUID> idsComTurmaSet = Set.copyOf(idsComTurma);

        Map<UUID, List<String>> instrumentosPorAluno = this.matriculaAlunoTurmaRepository
                .findInstrumentosDeAlunosComMatriculaAtiva(alunosIds)
                .stream()
                .collect(Collectors.groupingBy(
                        AlunoInstrumentoProjection::getAlunoId,
                        Collectors.mapping(AlunoInstrumentoProjection::getInstrumento, Collectors.toList())
                ));

        List<MembroResponsavel> responsaveis =
                this.membroResponsavelRepository.findByMembroMenor_IdIn(alunosIds);

        Map<UUID, List<MembroResponsavel>> responsaveisPorAluno =
                responsaveis.stream()
                        .collect(Collectors.groupingBy(
                                responsavel -> responsavel.getMembroMenor().getId()
                        ));

        var resultado = alunos.map(aluno -> this.alunoMapper.toResponse(
                aluno,
                idsComTurmaSet.contains(aluno.getId()),
                instrumentosPorAluno.getOrDefault(aluno.getId(), List.of()),
                responsaveisPorAluno.getOrDefault(aluno.getId(), List.of())
        ));

        return pageMapper.toResponse(resultado);
    }
}
