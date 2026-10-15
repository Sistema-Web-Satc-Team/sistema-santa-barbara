package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.model.Membro;

import java.util.List;
import java.util.UUID;

public record MembroResumidoResponse(
        UUID id,
        String nome,
        String email,
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
