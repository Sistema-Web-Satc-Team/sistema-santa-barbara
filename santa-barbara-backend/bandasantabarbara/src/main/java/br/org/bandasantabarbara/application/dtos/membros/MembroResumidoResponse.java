package br.org.bandasantabarbara.application.dtos.membros;

import java.util.List;
import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identificação resumida de um membro.")
public record MembroResumidoResponse(
        @Schema(description = "Identificador do membro.", format = "uuid")
        UUID id,
        @Schema(description = "Nome completo.")
        String nome,
        @Schema(description = "E-mail de contato.", format = "email")
        String email,
        @Schema(description = "Telefone de contato.")
        String telefone
) {
    public static MembroResumidoResponse deEntidade(Membro membro) {

        return new MembroResumidoResponse(
                membro.getId(),
                membro.getNome(),
                membro.getEmail(),
                membro.getTelefone()
        );
    }
}
