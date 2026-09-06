package br.org.bandasantabarbara.application;

import br.org.bandasantabarbara.exception.InvalidoException;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.infrastructure.security.TokenService;
import br.org.bandasantabarbara.repositories.MembroRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticarMembroUseCase {

    private final MembroRepository membroRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticarMembroUseCase(
            MembroRepository membroRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.membroRepository = membroRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public TokenResponse executar(LoginRequest request) {
        var membro = membroRepository.findByUsernameOuEmail(request.login())
                .orElseThrow(() -> new InvalidoException("Usuário ou senha incorreto."));

        boolean senhaValida = passwordEncoder.matches(request.senha(), membro.getCredencial().getHashSenha());

        if (!senhaValida) {
            throw new InvalidoException("Usuário ou senha incorreto.");
        }

        String token = tokenService.gerarToken(membro);
        return TokenResponse.bearer(token, tokenService.getTempoExpiracaoSegundos());
    }

}