package br.org.bandasantabarbara.application.dtos.convite;

import lombok.Getter;
import lombok.Setter;

public class MembroDestinatarioDTO {
    @Getter @Setter
    String email;

    @Getter @Setter
    String telefone;

    @Getter @Setter
    String idMembro;
}
