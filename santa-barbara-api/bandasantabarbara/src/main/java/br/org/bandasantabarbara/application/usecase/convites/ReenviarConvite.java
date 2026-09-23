package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.convite.ReenviarConviteRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.convite.MembroDestinatarioDTO;
import br.org.bandasantabarbara.application.events.ConvidarMembroEvento;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.ConviteStatus;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReenviarConvite {


    private final ConviteRepository conviteRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    public ReenviarConvite(
            ConviteRepository conviteRepository,
            ApplicationEventPublisher eventPublisher) {
        this.conviteRepository = conviteRepository;
        this.eventPublisher = eventPublisher;
    }

    public ConvidarMembroResponse executar(ReenviarConviteRequest request) {

        Convite convite = conviteRepository
                .findById(request.idConvite())
                .orElseThrow(() -> new NaoEncontradoException("Convite não encontrado."));

        if (convite.getDataExpiracaoConvite().isBefore(Instant.now())) {
            if (convite.getStatus() != ConviteStatus.EXPIRADO) {
                convite.setStatus(ConviteStatus.EXPIRADO);
                this.conviteRepository.save(convite);
            }

            throw new RuntimeException("Convite expirado.");
        }

        convite.setStatus(ConviteStatus.REENVIADO);

        this.conviteRepository.save(convite);

        // Possa ser que existem detalhes de infraestrutura acoplado com o caso de uso!
        String urlConvite = frontEndUrl + convite.getId();

        Membro membro = convite.getMembro();

        MembroDestinatarioDTO destinatarioDto = new MembroDestinatarioDTO();
        destinatarioDto.setEmail(membro.getEmail());
        destinatarioDto.setTelefone(membro.getTelefone());
        destinatarioDto.setIdMembro(membro.getId().toString());

        this.eventPublisher.publishEvent(
                new ConvidarMembroEvento(
                        convite.getId().toString(),
                        urlConvite,
                        destinatarioDto
                )
        );

        return new ConvidarMembroResponse(
                convite.getId().toString(),
                urlConvite,
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite()
        );
    }
}
