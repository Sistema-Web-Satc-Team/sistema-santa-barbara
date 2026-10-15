package br.org.bandasantabarbara.application.dtos.convite;

public record EnviarEmailConviteRequest(String idConvite, String URL, MembroDestinatarioDTO membro) {
}
