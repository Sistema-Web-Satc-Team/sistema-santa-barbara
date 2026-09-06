package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.RegistrarAdminRequest;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Senha;
import br.org.bandasantabarbara.repositories.SetupApplicationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SetupApplicationUsecase {

    private final SetupApplicationRepository setupAppRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupApplicationUsecase(
            SetupApplicationRepository setupAppRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.setupAppRepository = setupAppRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public void registrarAdmin(RegistrarAdminRequest dto) {

        var senha = Senha.criar(dto.senha());

        var hashPassword = passwordEncoder.encode(senha.valor());

        var membro = Membro.criarAdministrador(
                dto.email(),
                dto.username(),
                hashPassword
        );

        setupAppRepository.salvarAdmin(membro);
    }


}
