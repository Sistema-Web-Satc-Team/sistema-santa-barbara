package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Membro;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.util.List;

@Component
public class PerfilMapper {

    private FuncaoMapper funcaoMapper;

    public PerfilMapper(FuncaoMapper funcaoMapper) {
        this.funcaoMapper = funcaoMapper;
    }


    public PerfilResponse toResponse(Membro membro, String nomeUsuario, List<Funcao> papeis, String avatarURL) {

        return new PerfilResponse(
                membro.getId(),
                membro.getNome(),
                nomeUsuario,
                membro.getEmail(),
                membro.getTelefone(),
                membro.getEndereco(),
                membro.getDataNascimento(),
                membro.getSexo(),
                membro.getIdade(),
                papeis.stream().map(this.funcaoMapper::toResponse).toList(),
                avatarURL
        );

    }

    public void toUpdate(Membro membro, PerfilRequest request) {

        if (request.endereco() != null) {
            membro.setEndereco(request.endereco());
        }

        if (request.telefone() != null) {
            membro.setTelefone(request.telefone());
        }
    }
}
