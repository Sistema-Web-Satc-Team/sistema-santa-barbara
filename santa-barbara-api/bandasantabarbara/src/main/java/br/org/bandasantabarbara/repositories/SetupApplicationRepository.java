package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;

import java.util.UUID;

public interface SetupApplicationRepository {
    void salvarAdmin(
            Membro membro
    );

}

