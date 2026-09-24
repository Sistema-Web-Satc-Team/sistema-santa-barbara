package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.convite.AceitarConviteRequest;
import br.org.bandasantabarbara.exception.ApplicationException;
import br.org.bandasantabarbara.exception.ExpiradoException;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.*;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class AceitarConvite {

    private final ConviteRepository conviteRepository;
    private final MembroRepository membroRepository;
    private final PasswordEncoder passwordEncoder;

    public AceitarConvite(
            ConviteRepository conviteRepository,
            MembroRepository membroRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.conviteRepository = conviteRepository;
        this.membroRepository = membroRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public void executar(AceitarConviteRequest request) {

        Convite convite = this.conviteRepository
                .findById(request.idConvite())
                .orElseThrow(() -> new NaoEncontradoException("Convite não encontrado"));

        boolean statusPermitido = convite.getStatus() == ConviteStatus.ENVIADO
                || convite.getStatus() == ConviteStatus.REENVIADO;

        if (!statusPermitido) {
            throw new ExpiradoException("Este convite não está mais disponível para aceite.");
        }

        if (convite.getDataExpiracaoConvite().isBefore(Instant.now())) {
            convite.setStatus(ConviteStatus.EXPIRADO);
            this.conviteRepository.save(convite);
            throw new ExpiradoException("Este convite já expirou.");
        }

        Membro membro = convite.getMembro();
        var senha = Senha.criar(request.senha());
        var hashPassword = passwordEncoder.encode(senha.valor());

        membro.definirCredenciaisPrimeiroAcesso(hashPassword, request.nomeDeUsuario());
        convite.setStatus(ConviteStatus.ACEITO);

    }

}
