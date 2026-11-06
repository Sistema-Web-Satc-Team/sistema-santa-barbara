package br.org.bandasantabarbara.application.services.membro.convite;

import br.org.bandasantabarbara.model.membro.Convite;

public interface CanalEnvioConviteStrategy {
    boolean enviar(Convite convite, String urlConvite);
    EnumTipoCanal getTipoCanal();
}
