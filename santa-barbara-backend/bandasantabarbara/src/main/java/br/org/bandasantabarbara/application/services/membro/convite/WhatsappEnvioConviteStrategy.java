package br.org.bandasantabarbara.application.services.membro.convite;

import org.springframework.stereotype.Component;

import br.org.bandasantabarbara.model.membro.Convite;


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
