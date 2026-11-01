package br.org.bandasantabarbara.application.dtos.convite;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Conteúdo preparado para envio de um convite por e-mail.")
public record EnviarEmailConviteRequest(String idConvite, String URL, MembroDestinatarioDTO membro) {
}
