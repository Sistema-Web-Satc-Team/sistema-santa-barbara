package br.org.bandasantabarbara.application.services.membro;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.AceitarConviteRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConsultarConviteResponse;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.convite.ReenviarConviteRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.services.membro.convite.CanalEnvioConviteFactory;
import br.org.bandasantabarbara.application.services.membro.convite.CanalEnvioConviteStrategy;
import br.org.bandasantabarbara.application.services.membro.convite.EnumTipoCanal;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Convite;
import br.org.bandasantabarbara.model.membro.EnumConviteStatus;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroCredencial;
import br.org.bandasantabarbara.model.membro.Senha;
import br.org.bandasantabarbara.repositories.membro.ConviteRepository;
import br.org.bandasantabarbara.repositories.membro.MembroCredencialRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;

@Service
public class ConviteService {
    
    private final ConviteRepository conviteRepository;
    private final MembroCredencialRepository membroCredencialRepository;
    private final PasswordEncoder passwordEncoder;

    private final MembroRepository membroRepository;
    private final CanalEnvioConviteFactory factoryEnvioConvite;

    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    private final PageMapper pageMapper;

    public ConviteService(
        ConviteRepository conviteRepository,
        PasswordEncoder passwordEncoder,
        MembroCredencialRepository membroCredencialRepository,
        PageMapper pageMapper,
        MembroRepository membroRepository,
        CanalEnvioConviteFactory factoryEnvioConvite
    ) {
        this.conviteRepository = conviteRepository;
        this.passwordEncoder = passwordEncoder;
        this.membroCredencialRepository = membroCredencialRepository;
        this.pageMapper = pageMapper;
        this.membroRepository = membroRepository;
        this.factoryEnvioConvite = factoryEnvioConvite;
    }


    @Transactional
    public void aceitarConvite(AceitarConviteRequest request) {

        Convite convite = this.conviteRepository
                .findById(request.idConvite())
                .orElseThrow(() -> new NotFoundException("Convite não encontrado"));

        convite.verificarSePodeSerAceito();
        convite.aceitar();

        Membro membro = convite.getMembro();
        var senha = Senha.criar(request.senha());
        var hashPassword = passwordEncoder.encode(senha.valor());

        var credencialDeAcesso = new MembroCredencial(membro);

        credencialDeAcesso.setHashSenha(hashPassword);
        credencialDeAcesso.setNomeUsuario(request.nomeDeUsuario());

        this.membroCredencialRepository.save(credencialDeAcesso);
    }

    public ConsultarConviteResponse pegarPorId(UUID idConvite) {

        Convite convite = this.conviteRepository
                .findById(idConvite)
                .orElseThrow(() -> new NotFoundException("Convite não encontrado ou inexistente."));

        String urlConvite = frontEndUrl + convite.getId();


        Membro membro = convite.getMembro();

        // MAPPER-03: ConviteMapper

        return new ConsultarConviteResponse(
                convite.getId(),
                urlConvite,
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus(),
                MembroResumidoResponse.deEntidade(membro)
        );
    }

    public PageResponse<ConsultarConviteResponse> listar(String search, EnumConviteStatus status, Pageable pageable) {


        Page<Convite> convites = this.conviteRepository.listarTodosConvites(search, status, pageable);

        Page<ConsultarConviteResponse> paginaResponse = convites.map(convite -> new ConsultarConviteResponse(
                convite.getId(),
                frontEndUrl + convite.getId(),
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus(),
                new MembroResumidoResponse(
                        convite.getMembro().getId(),
                        convite.getMembro().getNome(),
                        convite.getMembro().getEmail(),
                        convite.getMembro().getTelefone()
                )
        ));

        return this.pageMapper.toResponse(paginaResponse);
    }

    public boolean isConviteValido(UUID idConvite) {
        return conviteRepository
                .findById(idConvite)
                .map(Convite::podeSerAceito)
                .orElse(false);
    }

    public ConvidarMembroResponse convidar(ConvidarMembroRequest request) {

        Membro membro = membroRepository
                .findById(request.idMembro())
                .orElseThrow(() -> new NotFoundException("Membro não encontrado."));

        conviteRepository
                .findFirstByMembroIdOrderByDataCriacaoConviteDesc(membro.getId())
                .ifPresent(Convite::verificarSePodeSerEnviado);

        Convite convite = new Convite(membro);

        this.conviteRepository.save(convite);

        return processarEnvio(convite, request.canal());
    }

    public ConvidarMembroResponse reenviarConvite(ReenviarConviteRequest request) {
        Convite convite = conviteRepository
                .findById(request.idConvite())
                .orElseThrow(() -> new NotFoundException("Convite não encontrado."));

        convite.verificarSePodeSerReenviado();

        return processarEnvio(convite, request.canal());
    }


    private ConvidarMembroResponse processarEnvio(Convite convite, EnumTipoCanal tipoCanal) {
        String urlConvite = frontEndUrl + convite.getId();

        CanalEnvioConviteStrategy canal = this.factoryEnvioConvite.obter(tipoCanal);
        boolean envioComSucesso = canal.enviar(convite, urlConvite);

        if (envioComSucesso) {
            convite.registrarEnvioComSucesso();
        } else {
            convite.registrarFalhaEnvio();
        }

        this.conviteRepository.save(convite);

        // MAPPER-03: ConviteMapper

        return new ConvidarMembroResponse(
                convite.getId().toString(),
                urlConvite,
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus()
        );
    }
}
