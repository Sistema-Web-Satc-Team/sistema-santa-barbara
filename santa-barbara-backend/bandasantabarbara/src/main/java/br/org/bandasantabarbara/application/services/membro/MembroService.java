package br.org.bandasantabarbara.application.services.membro;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.RegistrarMembroMenorRequest;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroRelacaoRequest;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroResponsaveisResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.ResponsavelMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import br.org.bandasantabarbara.model.membro.MembroResponsavel.TipoRelacaoResponsavel;
import br.org.bandasantabarbara.model.membro.MembroResponsavelId;
import br.org.bandasantabarbara.repositories.membro.MembroCredencialRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;
import br.org.bandasantabarbara.repositories.membro.MembroResponsavelRepository;
import br.org.bandasantabarbara.repositories.membro.MembroSpecs;
import br.org.bandasantabarbara.repositories.membro.MembroVinculoRepository;

@Service
public class MembroService {

    private final MembroRepository membroRepository;
    private final MembroCredencialRepository membroCredencialRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final MembroVinculoRepository membroVinculoRepository;

    private final MembroMapper membroMapper;
    private final PageMapper pageMapper;
    private final ResponsavelMapper responsavelMapper;

    public MembroService(
        MembroRepository membroRepository,
        MembroMapper membroMapper,
        MembroResponsavelRepository membroResponsavelRepository,
        PageMapper pageMapper,
        MembroCredencialRepository membroCredencialRepository,
        ResponsavelMapper responsavelMapper,
        MembroVinculoRepository membroVinculoRepository
    ) {
        this.membroRepository = membroRepository;
        this.membroMapper = membroMapper;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.pageMapper = pageMapper;
        this.membroCredencialRepository = membroCredencialRepository;
        this.responsavelMapper = responsavelMapper;
        this.membroVinculoRepository = membroVinculoRepository;
    }

    public MembroResponse cadastrarMaiorIdade(MembroRequest request) {
       var membro = this.membroMapper.toEntity(request);

        if ( membro.ehMenorDeIdade() ) 
           throw new BadRequestException("Membro cadastrado não pode ser menor de idade.");

        if (membroRepository.existsByEmailIgnoreCase(membro.getEmail())) 
            throw new ConflictException("Este e-mail já está cadastrado.");


       this.membroRepository.save(membro);
       return this.membroMapper.toResponse(membro);
    }


    @Transactional
    public MembroResponse cadastrarMenorIdade(
            RegistrarMembroMenorRequest request
    ) {
        Membro responsavel = membroRepository
            .findById(request.idResponsavel())
            .orElseThrow(() ->
                    new NotFoundException("Responsável não encontrado.")
            );


        Membro menor = membroMapper.toEntityMenor(request.menor());

        if (!menor.getEmail().isBlank())
            if (membroRepository.existsByEmailIgnoreCase(menor.getEmail())) 
                throw new ConflictException("Este e-mail já está cadastrado.");

        if (!menor.ehMenorDeIdade()) {
            throw new BadRequestException(
                    "Membro informado não é menor de idade."
            );
        }

        menor = membroRepository.save(menor);

        // MAPPER-01: MembroResponsavelMapper

        salvarRelacaoResponsavel(responsavel, menor, request.grauRelacao());

        return membroMapper.toResponse(menor);
    }

    @Transactional(readOnly = true)
    public PageResponse<MembroResponse> listarMembros(
        String busca, 
        Boolean maiorDeIdade, 
        Boolean semAcesso, 
        Pageable pageable
    ) {

        Page<Membro> membrosPage = buscarMembrosComFiltros(
            busca,
            maiorDeIdade,
            semAcesso,
            pageable
        );

        if (!membrosPage.isEmpty()) {
            List<UUID> ids = membrosPage.getContent()
                .stream()
                .map(Membro::getId)
                .toList();

            membroRepository.findAllWithVinculosByIds(ids);
        }

        return this.pageMapper.toResponse(membrosPage.map(
                this.membroMapper::toResponse
        ));
    }

    @Transactional
    public MembroResponse atualizar(UUID membroId, MembroRequest request) {

        var membro = this.membroRepository.findById(membroId)
                .orElseThrow(
                        () -> new NotFoundException("Membro não encontrado.")
                );


        this.membroCredencialRepository.findByIdWithMembro(membro.getId())
            .ifPresent((credencial) -> {
                if (credencial.isAtivo() && request.email() != null && !request.email().equalsIgnoreCase(membro.getEmail())) {
                    throw new BadRequestException("Email não pode ser alterado com membro ativo.");
                }
        });

        var foiMenorIdade = membro.ehMenorDeIdade();

        if (request.email() != null &&
            membroRepository.existsByEmailIgnoreCaseAndIdNot(
                request.email(),
                membroId
            )) {
            throw new ConflictException(
                "Este e-mail já está cadastrado para outro membro."
            );
        }

        this.membroMapper.toUpdate(request, membro);

        // Em termos lógico:
        // ¬A ∧ B = B ∧ ¬A
        if (membro.ehMenorDeIdade() && !foiMenorIdade) {
            var responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroId);

            if (responsaveis.isEmpty()) {
                throw new BadRequestException("Para o membro se tornar de menor é necessário atribuir pelo menos um responsável antes.");
            }


        }

        this.membroRepository.save(membro);

        return  this.membroMapper.toResponse(membro);
    }

    @Transactional
    public void adicionarResponsavelAoDependente(
        UUID idResponsavel, 
        UUID idMembro, 
        MembroRelacaoRequest request
    ) {
        Membro responsavel = this.membroRepository
            .findById(idResponsavel)
            .orElseThrow(
                () -> new NotFoundException("Responsável informado não encontrado")
            );

        Membro dependente = this.membroRepository
            .findById(idMembro)
            .orElseThrow(
                () -> new NotFoundException("Membro dependente informado não encontrado")
            );

        if (jaEhResponsavel(idResponsavel, idMembro))
            return;
        
        var relacao = salvarRelacaoResponsavel(responsavel, dependente, request.grauRelacao());

        this.membroResponsavelRepository.save(relacao);
    }

    @Transactional(readOnly = true)
    public MembroResponsaveisResponse consultarResponsaveisDoMembro(UUID membroId) {
        Membro dependente = this.membroRepository.findById(membroId).orElseThrow(() -> new NotFoundException("Membro informado não encontrado."));

        List<MembroResponsavel> responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroId);

        return new MembroResponsaveisResponse(
                this.membroMapper.toResponse(dependente),
                responsaveis.stream().map(this.responsavelMapper::toResponse).toList()
        );
    }

    public void removerResponsavelDoMembro(UUID idResponsavel, UUID idMembro) {
        Membro responsavel = this.membroRepository
            .findById(idResponsavel)
            .orElseThrow(
                () -> new NotFoundException("Responsável informado não encontrado")
            );

        Membro membroDependente = this.membroRepository
            .findById(idMembro)
            .orElseThrow(
                () -> new NotFoundException("Membro dependente informado não encontrado")
            );

        if (!jaEhResponsavel(idResponsavel, idMembro)) 
            return;
        

        if (membroDependente.ehMenorDeIdade()) {
            List<MembroResponsavel> responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroDependente.getId());

            if (responsaveis.size() <= 1) {
                throw new BadRequestException("Um membro de menor deve ter pelo menos um responsável cadastrado.");
            }

        }

        MembroResponsavelId membroResponsavelId = new MembroResponsavelId();

        membroResponsavelId.setIdMembroDependente(membroDependente.getId());
        membroResponsavelId.setIdMembroResponsavel(responsavel.getId());

        MembroResponsavel membroResponsavel = this.membroResponsavelRepository.findById(membroResponsavelId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        this.membroResponsavelRepository.delete(membroResponsavel);

    }

    public Page<Membro> buscarMembrosComFiltros(
        String busca, 
        Boolean maiorDeIdade, 
        Boolean semAcesso, 
        Pageable pageable
    ) {
        Specification<Membro> spec = Specification
                .where(MembroSpecs.porNomeOuEmail(busca))
                .and(MembroSpecs.porMaioridade(maiorDeIdade))
                .and(MembroSpecs.semAcesso(semAcesso));

        return membroRepository.findAll(spec, pageable); 
    }

    @Transactional
    public void remover(UUID membroId) {
        Membro membro = membroRepository.findByIdForUpdate(membroId)
            .orElseThrow(() -> new NotFoundException("Membro não encontrado."));

        if (membroVinculoRepository.existsByMembro_Id(membroId)) {
            throw new BadRequestException("Não é possível remover um membro que possui vínculos.");
        }

        if (membroCredencialRepository.existsByIdMembro(membroId)) {
            throw new BadRequestException("Não é possível remover um membro que possui credencial associada.");
        }

        membroRepository.delete(membro);
    }

    private MembroResponsavel salvarRelacaoResponsavel(
        Membro responsavel, 
        Membro menor, 
        TipoRelacaoResponsavel grauRelacao
    ) {
        var relacao = new MembroResponsavel();
        relacao.setResponsavel(responsavel);
        relacao.setMembroMenor(menor);
        relacao.setTipoRelacao(grauRelacao);
        this.membroResponsavelRepository.save(relacao);
        return relacao;
    }

    private boolean jaEhResponsavel(UUID idResponsavel, UUID idMembro) {
        return this.membroResponsavelRepository
            .existsByResponsavel_IdAndMembroMenor_Id(idResponsavel, idMembro);
    }
}
