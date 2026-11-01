package br.org.bandasantabarbara.application.dtos.convite;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de contato do membro destinatário do convite.")
public class MembroDestinatarioDTO {
    @Schema(description = "E-mail do destinatário.", example = "membro@santabarbara.org")
    @Getter @Setter
    String email;

    @Schema(description = "Telefone do destinatário.", example = "+5548999999999")
    @Getter @Setter
    String telefone;

    @Schema(description = "Identificador do membro.")
    @Getter @Setter
    String idMembro;
}
