package br.org.bandasantabarbara.application.usecase.papeis;

import br.org.bandasantabarbara.exception.ApplicationException;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.model.Permissao;
import br.org.bandasantabarbara.repositories.PapelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarPermissoesDoPapel {

    private final PapelRepository papelRepository;

    public ListarPermissoesDoPapel(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }

    @Transactional(readOnly = true)
    public List<String> executar(String nome) {

        var papel = Papel.formatar(nome);

        var papelEncontrado = papelRepository
                .findByNomeWithPermissoes(papel.getNome())
                .orElseThrow(() ->
                        new NaoEncontradoException("Papel não encontrado.")
                );

        return papelEncontrado.getPermissoes()
                .stream()
                .map(Permissao::getNome)
                .toList();
    }
}