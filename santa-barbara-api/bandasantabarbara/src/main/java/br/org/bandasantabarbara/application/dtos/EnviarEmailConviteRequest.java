package br.org.bandasantabarbara.application.events;

import br.org.bandasantabarbara.application.dtos.convite.MembroDestinatarioDTO;

public record ConvidarMembroEvento(String idConvite, String URL, MembroDestinatarioDTO membro) {
}
