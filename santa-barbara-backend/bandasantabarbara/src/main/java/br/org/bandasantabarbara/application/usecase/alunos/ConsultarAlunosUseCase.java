package br.org.bandasantabarbara.application.usecase.alunos;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponsavelResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.dtos.alunos.InstrumentoAlunoResponse;
import br.org.bandasantabarbara.application.mapper.GrauRelacaoMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.repositories.AlunoProjection;
import br.org.bandasantabarbara.repositories.AlunoRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsultarAlunosUseCase {

    private final AlunoRepository alunoRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final PageMapper pageMapper;
    private final GrauRelacaoMapper grauRelacaoMapper;

    public ConsultarAlunosUseCase(
            AlunoRepository alunoRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            PageMapper pageMapper,
            GrauRelacaoMapper grauRelacaoMapper
    ) {
        this.alunoRepository = alunoRepository;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.pageMapper = pageMapper;
        this.grauRelacaoMapper = grauRelacaoMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<AlunoResponse> execute(Pageable pageable) {
        // TO-DO: Considerar agendado também
        // TO-DO: Retornar se tem turma ou não
        // TO-DO: Pegar os intrumentos que o aluno esta aprendendo na turma
        var alunos = alunoRepository.buscarPorStatus("ATIVO", pageable);

        var alunosIds = alunos.getContent()
                .stream()
                .map(AlunoProjection::getId)
                .toList();

        List<MembroResponsavel> responsaveis = alunosIds.isEmpty()
                ? List.of()
                : membroResponsavelRepository.findByMembroMenor_IdIn(alunosIds);

        Map<java.util.UUID, List<MembroResponsavel>> responsaveisPorAluno =
                responsaveis.stream()
                        .collect(Collectors.groupingBy(
                                responsavel -> responsavel.getMembroMenor().getId()
                        ));

        var resultado = alunos.map(aluno -> {
            var responsaveisDoAluno = responsaveisPorAluno
                    .getOrDefault(aluno.getId(), List.of())
                    .stream()
                    .map(responsavel -> new AlunoResponsavelResponse(
                            responsavel.getResponsavel().getId(),
                            responsavel.getResponsavel().getNome(),
                            responsavel.getResponsavel().getEmail(),
                            responsavel.getResponsavel().getTelefone(),
                            this.grauRelacaoMapper.toResponse(
                                    responsavel.getResponsavel().getSexo(),
                                    responsavel.getTipoRelacao()
                            )
                    ))
                    .toList();

            int idade = Period.between(
                    aluno.getDataNascimento(),
                    LocalDate.now()
            ).getYears();

            return new AlunoResponse(
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getEmail(),
                    aluno.getTelefone(),
                    aluno.getSexo(),
                    idade,
                    false,
                    new InstrumentoAlunoResponse(List.of()),
                    responsaveisDoAluno
            );
        });

        return pageMapper.toResponse(resultado);
    }


}
