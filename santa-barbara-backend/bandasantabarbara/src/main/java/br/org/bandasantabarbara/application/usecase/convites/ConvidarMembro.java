package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.convite.ReenviarConviteRequest;
import br.org.bandasantabarbara.application.services.convite.CanalEnvioConviteFactory;
import br.org.bandasantabarbara.application.services.convite.CanalEnvioConviteStrategy;
import br.org.bandasantabarbara.application.services.convite.EnumTipoCanal;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ConvidarMembro {

    private final MembroRepository membroRepository;
    private final ConviteRepository conviteRepository;
    private final CanalEnvioConviteFactory factoryEnvioConvite;


    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    public ConvidarMembro(
            MembroRepository membroRepository,
            ConviteRepository conviteRepository,
            CanalEnvioConviteFactory factoryEnvioConvite) {
        this.membroRepository = membroRepository;
        this.conviteRepository = conviteRepository;
        this.factoryEnvioConvite = factoryEnvioConvite;
    }

    public ConvidarMembroResponse convidar(ConvidarMembroRequest request) {

        Membro membro = membroRepository
                .findById(request.idMembro())
                .orElseThrow(() -> new NotFoundException("Membro não encontrado."));

        conviteRepository
                .findFirstByMembroIdOrderByDataCriacaoConviteDesc(membro.getId())
                .ifPresent(Convite::verificarSePodeSerEnviado);

        Convite convite = new Convite(membro);

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

        return new ConvidarMembroResponse(
                convite.getId().toString(),
                urlConvite,
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus()
        );
    }

}
