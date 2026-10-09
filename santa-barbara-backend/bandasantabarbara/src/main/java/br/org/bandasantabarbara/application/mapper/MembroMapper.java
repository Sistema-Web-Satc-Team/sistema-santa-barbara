package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.membros.MembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.model.Membro;
import org.springframework.stereotype.Component;


@Component
public class MembroMapper {

    public Membro toEntity(MembroRequest request) {
        var membro = new Membro();

        membro.setNome(request.nome());

        if (request.endereco() != null) {
            membro.setEndereco(request.endereco());
        }

        membro.setEmail(request.email());
        membro.setSexo(request.sexo());
        membro.setDataNascimento(request.dataNascimento());

        if (request.telefone() != null) {
            membro.setTelefone(request.telefone());
        }

        return membro;
    }

    public Membro toEntityMenor(MembroRequest request) {
        var membro = new Membro();

        membro.setNome(request.nome());

        if (request.endereco() != null) {
            membro.setEndereco(request.endereco());
        }

        if (request.email() != null) {
            membro.setEmail(request.email());
        }

        membro.setSexo(request.sexo());
        membro.setDataNascimento(request.dataNascimento());

        if (request.telefone() != null) {
            membro.setTelefone(request.telefone());
        }

        return membro;
    }

    public Membro toUpdate(MembroRequest request, Membro membro) {
        if (request.nome() != null) {
            membro.setNome(request.nome());
        }

        if (request.telefone() != null) {
            membro.setTelefone(request.telefone());
        }

        if (request.endereco() != null) {
            membro.setEndereco(request.endereco());
        }

        if (request.dataNascimento() != null) {
            membro.setDataNascimento(request.dataNascimento());
        }

        if (request.sexo() != null) {
            membro.setSexo(request.sexo());
        }

        if (request.email() != null) {
            membro.setEmail(request.email());
        }

        return membro;
    }

    public MembroResponse toResponse(Membro membro) {
        return new MembroResponse(
          membro.getId(),
          membro.getNome(),
          membro.getTelefone(),
          membro.getEndereco(),
          membro.getEmail(),
          membro.getDataNascimento(),
          membro.getSexo(),
          membro.getIdade(),
          membro.hasVinculoAtivo(),
          membro.ehMenorDeIdade(),
          membro.getStatusCredencial()
        );
    }
}
