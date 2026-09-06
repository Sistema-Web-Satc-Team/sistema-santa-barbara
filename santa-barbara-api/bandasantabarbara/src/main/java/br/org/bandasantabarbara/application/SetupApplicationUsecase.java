package br.org.bandasantabarbara.application;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.model.Senha;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.SetupApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SetupApplicationUsecase {

    private SetupApplicationRepository setupAppRepository;
    private PasswordEncoder passwordEncoder;

    public SetupApplicationUsecase(
            SetupApplicationRepository setupAppRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.setupAppRepository = setupAppRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public void registrarAdmin(RegisterAdminDTO dto) {

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
