package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;
import java.util.List;
import java.util.UUID;

public record MembroResumidoResponse(
        UUID id,
        String nome,
        String sobrenome,
        String email,
        String telefone,
        List<String> papeis
) {
    public static MembroResumidoResponse deEntidade(Membro membro) {
        List<String> papeis = membro.getPapeis().stream()
                .map(Papel::getNome)
                .toList();

        return new MembroResumidoResponse(
                membro.getId(),
                membro.getNome(),
                membro.getSobrenome(),
                membro.getEmail(),
                membro.getTelefone(),
                papeis
        );
    }
}
