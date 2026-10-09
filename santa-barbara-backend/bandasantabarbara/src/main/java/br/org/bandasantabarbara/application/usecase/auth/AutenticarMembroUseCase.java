package br.org.bandasantabarbara.application.usecase.auth;

import br.org.bandasantabarbara.application.dtos.auth.TokenRequest;
import br.org.bandasantabarbara.application.dtos.auth.LoginRequest;
import br.org.bandasantabarbara.application.dtos.auth.TokenResponse;
import br.org.bandasantabarbara.application.services.security.TokenService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.repositories.MembroCredencialRepository;

import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutenticarMembroUseCase {

    private final MembroVinculoRepository membroVinculoRepository;
    private final MembroCredencialRepository membroCredencialRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticarMembroUseCase(
            MembroVinculoRepository membroVinculoRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            MembroCredencialRepository membroCredencialRepository) {
        this.membroVinculoRepository = membroVinculoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.membroCredencialRepository = membroCredencialRepository;
    }

    @Transactional(readOnly = true)
    public TokenResponse executar(LoginRequest request) {
        var membroCredencial = membroCredencialRepository.findByLogin(request.login().trim())
                .orElseThrow(() -> new BadRequestException("Usuário ou senha incorreto."));


        boolean senhaValida = passwordEncoder.matches(request.senha(), membroCredencial.getHashSenha());

        if (!senhaValida) {
            throw new BadRequestException("Usuário ou senha incorreto.");
        }

        if (membroCredencial.isBloqueado()) {
            throw new ForbiddenException("Você foi bloqueado.");
        }

        if (!membroCredencial.isAtivo()) {
            throw new ForbiddenException("Você esta suspenso. Entre em contato com a organização para reativar.");
        }


        // Invariante interna em MembroVinculo e Funcao garante que nunca teremos mais vínculos que o número
        // de funções máximo que o sistema suporta.
        List<MembroVinculo> vinculos = membroVinculoRepository
            .findByMembroIdAndDataTerminoIsNullOrderByDataInicioDesc(
                membroCredencial.getMembro().getId()
            );


        if (vinculos.isEmpty()) {
            throw new BadRequestException("Você não tem nenhum vinculo ativo!");
        }


        List<String> papeis = vinculos.stream()
                .map(vinculo -> vinculo.getFuncao().getCode())
                .toList();


        String token = tokenService.gerarToken(
                new TokenRequest(
                        membroCredencial.getMembro().getId().toString(),
                        membroCredencial.getMembro().getEmail(),
                        membroCredencial.getNomeUsuario(),
                        papeis
                )
        );
        return TokenResponse.bearer(token, tokenService.getTempoExpiracaoSegundos());
    }

}