package br.org.bandasantabarbara.application.services.convite;

import br.org.bandasantabarbara.application.dtos.convite.EnviarEmailConviteRequest;
import br.org.bandasantabarbara.application.dtos.convite.MembroDestinatarioDTO;
import br.org.bandasantabarbara.model.Convite;
import org.springframework.stereotype.Component;


@Component
public class EmailEnvioConviteStrategy implements CanalEnvioConviteStrategy {

    private final EnviarEmailDeConvite enviarEmailConvite;

    public EmailEnvioConviteStrategy(EnviarEmailDeConvite enviarEmailConvite) {
        this.enviarEmailConvite = enviarEmailConvite;
    }

    @Override
    public boolean enviar(Convite convite, String urlConvite) {
        MembroDestinatarioDTO destinatarioDto = new MembroDestinatarioDTO();
        destinatarioDto.setEmail(convite.getMembro().getEmail());
        destinatarioDto.setIdMembro(convite.getMembro().getId().toString());

        return this.enviarEmailConvite.handle(new EnviarEmailConviteRequest(
                convite.getId().toString(),
                urlConvite,
                destinatarioDto
        ));
    }

    @Override
    public EnumTipoCanal getTipoCanal() {
        return EnumTipoCanal.EMAIL;
    }
}
