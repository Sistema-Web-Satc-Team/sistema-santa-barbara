package br.org.bandasantabarbara.application.services.convite;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CanalEnvioConviteFactory {

    private final Map<EnumTipoCanal, CanalEnvioConviteStrategy> estrategias;

    public CanalEnvioConviteFactory(List<CanalEnvioConviteStrategy> listaEstrategias) {
        this.estrategias = listaEstrategias.stream()
                .collect(Collectors.toMap(CanalEnvioConviteStrategy::getTipoCanal, Function.identity()));
    }

    public CanalEnvioConviteStrategy obter(EnumTipoCanal tipoCanal) {
        CanalEnvioConviteStrategy estrategia = estrategias.get(tipoCanal);
        if (estrategia == null) {
            throw new IllegalArgumentException("Canal de envio de convite não suportado: " + tipoCanal);
        }
        return estrategia;
    }
}