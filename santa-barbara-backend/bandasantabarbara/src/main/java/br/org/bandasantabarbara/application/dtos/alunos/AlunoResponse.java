package br.org.bandasantabarbara.application.dtos.alunos;

import br.org.bandasantabarbara.application.dtos.membros.ResponsavelResponse;
import br.org.bandasantabarbara.model.Membro;

import java.util.List;
import java.util.UUID;

public record AlunoResponse(
        UUID id,
        String nome,
        String email,
        String telefone,
        Membro.MembroSexoEnum sexo,
        int idade,
        boolean temTurma,
        InstrumentoAlunoResponse instrumentos,
        List<AlunoResponsavelResponse> responsaveis
) {

    public AlunoResponse {
        instrumentos = instrumentos == null ? new InstrumentoAlunoResponse(List.of()) : instrumentos;
        responsaveis = responsaveis == null ? List.of() : List.copyOf(responsaveis);
    }
}