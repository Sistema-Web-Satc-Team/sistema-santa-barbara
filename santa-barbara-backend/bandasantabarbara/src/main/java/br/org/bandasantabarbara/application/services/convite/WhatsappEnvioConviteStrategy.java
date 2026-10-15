package br.org.bandasantabarbara.application.services.convite;

import br.org.bandasantabarbara.model.Convite;
import org.springframework.stereotype.Component;


@Component
public class WhatsappEnvioConviteStrategy implements CanalEnvioConviteStrategy {


    @Override
    public boolean enviar(Convite convite, String urlConvite) {
        throw new RuntimeException("Must to be implemented");
    }

    @Override
    public EnumTipoCanal getTipoCanal() {
        return EnumTipoCanal.WHATSAPP;
    }
}
