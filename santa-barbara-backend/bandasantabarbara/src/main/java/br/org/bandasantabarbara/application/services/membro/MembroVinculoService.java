package br.org.bandasantabarbara.application.services.membro;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.events.MembroVinculoCriadoEvent;
import br.org.bandasantabarbara.application.mapper.VinculoMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Funcao;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroVinculo;
import br.org.bandasantabarbara.repositories.membro.FuncaoRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;
import br.org.bandasantabarbara.repositories.membro.MembroResponsavelRepository;
import br.org.bandasantabarbara.repositories.membro.MembroVinculoRepository;

@Service
public class MembroVinculoService {
     private ApplicationEventPublisher eventPublisher;

    private final MembroRepository membroRepository;
    private final MembroVinculoRepository membroVinculoRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final FuncaoRepository funcaoRepository;
    private final VinculoMapper vinculoMapper;


    // Débito técnico
    // Acomplamento temporário
    private static final Map<String, Set<String>> INCOMPATIBILIDADES = Map.of(
            "SUPER_ADMIN", Set.of("ALUNO", "SECRETARIO", "ADMIN", "PROFESSOR", "MUSICA_BANDA", "MAESTRO", "RESPONSAVEL"),
            "ADMIN", Set.of("ALUNO", "SECRETARIO", "SUPER_ADMIN", "PROFESSOR", "MUSICA_BANDA", "MAESTRO", "RESPONSAVEL")
    );

    private boolean isFuncaoIncompativel(String papelA, String papelB) {
        return (INCOMPATIBILIDADES.containsKey(papelA) && INCOMPATIBILIDADES.get(papelA).contains(papelB)) ||
                (INCOMPATIBILIDADES.containsKey(papelB) && INCOMPATIBILIDADES.get(papelB).contains(papelA));
    }

    public MembroVinculoService(
            MembroRepository membroRepository,
            MembroVinculoRepository membroVinculoRepository,
            FuncaoRepository funcaoRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            VinculoMapper vinculoMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.membroRepository = membroRepository;
        this.membroVinculoRepository = membroVinculoRepository;
        this.funcaoRepository = funcaoRepository;
        this.vinculoMapper = vinculoMapper;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.eventPublisher = eventPublisher;
    }


    public VinculoResponse vincularFuncao(
            VincularMembroRequest request,
            UUID membroId,
            String funcao
    ) {


        Membro membro = this.membroRepository.findByIdWithVinculos(
                membroId
        ).orElseThrow(
                ( ) -> new NotFoundException("Membro não encontrado.")
        );

        Funcao novaFuncao = this.funcaoRepository
            .findFuncaoByCodeOrNome(request.funcao())
            .orElseThrow(
                () -> new NotFoundException("Função inexistente no sistema. Verifique a lista de funções disponiveis!")
            );

        // DEBITO-01
        // Débito técnico
        // Permissoes dentro dos casos de uso.
        // Fere o SRP
        if (!funcao.equals("SUPER_ADMIN")) {

            if (funcao.equals("SECRETARIO") && novaFuncao.getTipo() != Funcao.TipoFuncaoEnum.DOMINIO) {
                throw new ForbiddenException("Secretários não tem permissão para vínculos de administradores");
            }

            if (funcao.equals("ADMIN") && novaFuncao.getCode().equals("SUPER_ADMIN")) {
                throw new ForbiddenException("Administradores não podem vincular um membro como super administrador.");
            }

        }

        var codigoNovoVinculo = novaFuncao.getCode();

        for ( var vinculoAberto : membro.getVinculosAbertos()) {

            var codigoVinculoAberto = vinculoAberto.getFuncao().getCode();

            if (codigoVinculoAberto.equals(codigoNovoVinculo)) {
                throw new BadRequestException("O membro já tem um vínculo igual ativo ou agendado.");
            }

            if (isFuncaoIncompativel(codigoVinculoAberto, codigoNovoVinculo)) {
                throw new BadRequestException("O cargo " + codigoVinculoAberto + " é incompatível com " + codigoNovoVinculo + ".");
            }

        }


        if (codigoNovoVinculo.equals("RESPONSAVEL")) {
            boolean possuiResponsabilidade =
                    membroResponsavelRepository
                            .existsByResponsavel_Id(membroId);

            if (!possuiResponsabilidade) {
                throw new ConflictException(
                        "A função RESPONSAVEL exige que o membro seja responsável por pelo menos uma pessoa."
                );
            }
        }


        var vinculo = new MembroVinculo(
                membro,
                novaFuncao,
                request.dataInicio()
        );


        this.membroVinculoRepository.save(vinculo);

        eventPublisher.publishEvent(new MembroVinculoCriadoEvent(
            membro.getId(), 
            novaFuncao.getCode()
        ));


        return this.vinculoMapper.toResponse(vinculo);
    }

    @Transactional
    public void cancelar(UUID membroId, int vinculoId, String papelAtor) {
        var vinculo = this.membroVinculoRepository.findByIdAndMembroId(vinculoId, membroId).orElseThrow(() -> new NotFoundException("Vínculo ou membro inexistente."));

        String codigoFuncaoAlvo = vinculo.getFuncao().getCode();

        // DEBITO-02
        if (papelAtor.equals("SECRETARIO")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN") || codigoFuncaoAlvo.equals("ADMIN")) {
                throw new ForbiddenException("Secretários não têm permissão para remover vínculos de administradores.");
            }
        }

        if (papelAtor.equals("ADMIN")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN")) {
                throw new ForbiddenException("Administradores não podem remover vínculos de Super Administradores.");
            }
        }

        if (codigoFuncaoAlvo.equals("SUPER_ADMIN")) {
            long superAdminsAtivos = membroVinculoRepository.countByFuncaoCodeAndAtivoTrue("SUPER_ADMIN");

            if (superAdminsAtivos <= 1) {
                throw new BadRequestException("Não é permitido remover o último Super Admin ativo do sistema.");
            }
        }

        vinculo.cancelar();
    }

    @Transactional
    public void encerrar(
        UUID idMembro, 
        int idVinculo, 
        String papelAtor
    ) {
        MembroVinculo vinculo = membroVinculoRepository.findByIdAndMembroId(idVinculo, idMembro)
                .orElseThrow(() -> new NotFoundException("Vínculo não encontrado para este membro."));


        String codigoFuncaoAlvo = vinculo.getFuncao().getCode();

        // DEBITO-03
        if (papelAtor.equals("SECRETARIO")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN") || codigoFuncaoAlvo.equals("ADMIN")) {
                throw new ForbiddenException("Secretários não têm permissão para remover vínculos de administradores.");
            }
        }

        if (papelAtor.equals("ADMIN")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN") || codigoFuncaoAlvo.equals("ADMIN")) {
                throw new ForbiddenException("Administradores não podem remover vínculos de Super Administradores.");
            }
        }

        if (codigoFuncaoAlvo.equals("SUPER_ADMIN")) {
            long superAdminsAtivos = membroVinculoRepository.countByFuncaoCodeAndAtivoTrue("SUPER_ADMIN");

            if (superAdminsAtivos <= 1) {
                throw new BadRequestException("Não é permitido remover o último Super Admin ativo do sistema.");
            }
        }


        vinculo.encerrar();
    }


    public MembroVinculosResponse pegarVinculosDeUmMembroPorId(UUID membroId) {

        Membro membro = this.membroRepository.findById(membroId)
                .orElseThrow(() -> new NotFoundException("Membro não encontado."));

        var vinculos = this.membroVinculoRepository.findByMembroId(membro.getId());

        var vinculosAtuais = vinculos.stream().filter((v) -> v.estaAgendado() || v.estaAtivo()).toList();
        var vinculosCancelados = vinculos.stream().filter(MembroVinculo::estaCancelado).toList();
        var vinculosEncerrados = vinculos.stream().filter(MembroVinculo::estaEncerrado).toList();


        var vinculosResponse = new VinculosResponse(
                vinculosAtuais.stream().map(vinculoMapper::toResponse).toList(),
                vinculosEncerrados.stream().map(vinculoMapper::toResponse).toList(),
                vinculosCancelados.stream().map(vinculoMapper::toResponse).toList()
        );

        // MAPPER-02 : MembroVinculoMapper

        return new MembroVinculosResponse(
                membro.getId(),
                membro.getNome(),
                membro.getDataNascimento(),
                membro.getIdade(),
                membro.getSexo(),
                vinculosResponse
        );
    }

    
}
