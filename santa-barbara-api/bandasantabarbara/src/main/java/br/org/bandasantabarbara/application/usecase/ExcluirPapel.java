package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.papel.ExcluirPapelRequest;
import br.org.bandasantabarbara.exception.ApplicationException;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.repositories.PapelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ExcluirPapel {

    private final PapelRepository papelRepository;

    public ExcluirPapel(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }

    @Transactional
    public void executar(ExcluirPapelRequest request){

        var papel = Papel.formatar(request.papel());

        int result = this.papelRepository.excluirSeNaoPossuirMembros(papel.getNome());

        if (result == 0) {
            throw new ApplicationException(
                    "O papel não existe ou possui membros associados."
            );
        }

    }
}
