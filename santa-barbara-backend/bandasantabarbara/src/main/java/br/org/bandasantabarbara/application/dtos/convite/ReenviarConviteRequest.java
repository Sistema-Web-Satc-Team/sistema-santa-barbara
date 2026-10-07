package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.application.services.convite.EnumTipoCanal;

import java.util.UUID;

public record ReenviarConviteRequest(UUID idConvite, EnumTipoCanal canal) {
}
