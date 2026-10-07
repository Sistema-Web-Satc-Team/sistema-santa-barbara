package br.org.bandasantabarbara.application.services.convite;

import br.org.bandasantabarbara.model.Convite;

public interface CanalEnvioConviteStrategy {
    boolean enviar(Convite convite, String urlConvite);
    EnumTipoCanal getTipoCanal();
}
