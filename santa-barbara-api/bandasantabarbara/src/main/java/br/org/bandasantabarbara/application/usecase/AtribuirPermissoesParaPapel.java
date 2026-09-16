package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.papel.AtribuirPermissaoRequest;
import br.org.bandasantabarbara.exception.ApplicationException;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.model.Permissao;
import br.org.bandasantabarbara.repositories.PapelRepository;
import br.org.bandasantabarbara.repositories.PermissaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AtribuirPermissoesParaPapel {
    private final PapelRepository papelRepository;
    private final PermissaoRepository permissaoRepository;

    public AtribuirPermissoesParaPapel(PapelRepository papelRepository, PermissaoRepository permissaoRepository) {
        this.papelRepository = papelRepository;
        this.permissaoRepository = permissaoRepository;
    }


    public void executar(AtribuirPermissaoRequest request) {
        // Impede que ele tente enviar um formato inválido logo de cara.
        Papel papelVerificado = new Papel(request.papel());

        Papel papel = this.papelRepository.findByNomeWithPermissoes(papelVerificado.getNome())
                .orElseThrow(() -> new ApplicationException("Esse papel não existe."));


        Set<Permissao> permissoes =  this.permissaoRepository.findByNomeIn(request.permissoes());

        if (permissoes.size() != request.permissoes().size()) {
            throw new ApplicationException("Uma ou mais permissões informadas não existem.");
        }

        papel.setPermissoes(permissoes);

        this.papelRepository.save(papel);
    }
}
