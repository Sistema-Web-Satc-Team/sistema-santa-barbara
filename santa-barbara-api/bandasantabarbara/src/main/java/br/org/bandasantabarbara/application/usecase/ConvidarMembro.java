package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;

public class ConvidarMembro {

    private MembroRepository membroRepository;
    private ConviteRepository conviteRepository;

    public ConvidarMembro(MembroRepository membroRepository, ConviteRepository conviteRepository) {
        this.membroRepository = membroRepository;
        this.conviteRepository = conviteRepository;
    }

    public ConvidarMembroResponse executar(ConvidarMembroRequest request) {

        return new ConvidarMembroResponse("", "");
    }

}
