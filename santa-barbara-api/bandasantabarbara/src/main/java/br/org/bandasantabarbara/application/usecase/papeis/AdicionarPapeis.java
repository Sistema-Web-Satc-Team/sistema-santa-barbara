package br.org.bandasantabarbara.application.usecase.papeis;

import br.org.bandasantabarbara.application.dtos.papel.AdicionarPapelRequest;
import br.org.bandasantabarbara.exception.ApplicationException;
import br.org.bandasantabarbara.exception.ConflitoException;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.repositories.PapelRepository;
import org.springframework.stereotype.Service;


@Service
public class AdicionarPapeis {
    private PapelRepository papelRepository;

    public AdicionarPapeis(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }


    public void executar(AdicionarPapelRequest request) {
        var papel = new Papel(request.nome());

        this.papelRepository.findByNome(papel.getNome())
                .ifPresent(p -> { throw new ConflitoException("Papel já existe."); });

        this.papelRepository.save(papel);
    }



}
