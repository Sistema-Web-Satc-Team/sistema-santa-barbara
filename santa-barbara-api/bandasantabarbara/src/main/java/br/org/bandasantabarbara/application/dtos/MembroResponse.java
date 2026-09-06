package br.org.bandasantabarbara.application.dtos;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

public record MembroResponse(
        UUID id,
        String nome,
        String sobrenome,
        String nomeUsuario,
        String telefone,
        String endereco,
        String email,
        List<String> papeis,
        Integer age,
        String dataNascimento
)
{
    public static MembroResponse deEntidade(Membro membro) {
        Integer idadeCalculada = null;
        if (membro.getDataNascimento() != null) {
            idadeCalculada = Period.between(membro.getDataNascimento(), LocalDate.now()).getYears();
        }

        List<String> papeis = membro.getPapeis().stream()
                .map(Papel::getNome)
                .toList();

        return new MembroResponse(
                membro.getId(),
                membro.getNome(),
                membro.getSobrenome(),
                membro.getNomeDeUsuario(),
                membro.getTelefone(),
                membro.getEndereco(),
                membro.getEmail(),
                papeis,
                idadeCalculada,
                membro.getDataNascimento() != null ? membro.getDataNascimento().toString() : null
        );
    }
}
