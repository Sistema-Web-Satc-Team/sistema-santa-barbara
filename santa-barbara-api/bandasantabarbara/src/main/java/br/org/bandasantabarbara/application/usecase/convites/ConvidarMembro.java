package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.convite.MembroDestinatarioDTO;
import br.org.bandasantabarbara.application.events.ConvidarMembroEvento;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.ConviteStatus;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ConvidarMembro {

    private final MembroRepository membroRepository;
    private final ConviteRepository conviteRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    public ConvidarMembro(
            MembroRepository membroRepository,
            ConviteRepository conviteRepository,
            ApplicationEventPublisher eventPublisher) {
        this.membroRepository = membroRepository;
        this.conviteRepository = conviteRepository;
        this.eventPublisher = eventPublisher;
    }

    public ConvidarMembroResponse executar(ConvidarMembroRequest request) {

        Membro membro = membroRepository
                .findById(request.idMembro())
                .orElseThrow(() -> new NaoEncontradoException("Membro não encontrado."));

        if (conviteRepository.existsByMembroAndDataExpiracaoConviteAfter(membro, Instant.now())) {
            throw new RuntimeException("Já existe um convite válido para este membro.");
        }

        Convite convite = new Convite(membro);

        // Talvez isso não seja o ideal...
        convite.setStatus(ConviteStatus.ENVIADO);

        this.conviteRepository.save(convite);

        // Possa ser que existem detalhes de infraestrutura acoplado com o caso de uso!
        String urlConvite = frontEndUrl + convite.getId();

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
