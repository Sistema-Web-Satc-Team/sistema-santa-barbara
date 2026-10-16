package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.convite.AceitarConviteRequest;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.*;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import br.org.bandasantabarbara.repositories.MembroCredencialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AceitarConvite {

    private final ConviteRepository conviteRepository;
    private final MembroCredencialRepository membroCredencialRepository;
    private final PasswordEncoder passwordEncoder;

    public AceitarConvite(
            ConviteRepository conviteRepository,
            PasswordEncoder passwordEncoder,
            MembroCredencialRepository membroCredencialRepository
    ) {
        this.conviteRepository = conviteRepository;
        this.passwordEncoder = passwordEncoder;
        this.membroCredencialRepository = membroCredencialRepository;
    }


    @Transactional
    public void executar(AceitarConviteRequest request) {

        Convite convite = this.conviteRepository
                .findById(request.idConvite())
                .orElseThrow(() -> new NotFoundException("Convite não encontrado"));

        convite.verificarSePodeSerAceito();
        convite.aceitar();

        Membro membro = convite.getMembro();
        var senha = Senha.criar(request.senha());
        var hashPassword = passwordEncoder.encode(senha.valor());

        var credencialDeAcesso = new MembroCredencial(membro);

        credencialDeAcesso.setHashSenha(hashPassword);
        credencialDeAcesso.setNomeUsuario(request.nomeDeUsuario());

        this.membroCredencialRepository.save(credencialDeAcesso);
    }

}
