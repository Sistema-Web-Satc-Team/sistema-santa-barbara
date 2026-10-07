package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.auth.LoginRequest;
import br.org.bandasantabarbara.application.dtos.auth.TokenResponse;
import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.usecase.auth.AutenticarMembroUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AutenticarMembroUseCase autenticarMembroUseCase;

    public AutenticacaoController(AutenticarMembroUseCase autenticarMembroUseCase) {
        this.autenticarMembroUseCase = autenticarMembroUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = autenticarMembroUseCase.executar(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", response.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(response.expiraEmEmSegundos())
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Login realizado com sucesso!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logout realizado com sucesso!"));
    }

    /*@GetMapping("/me")
    public ResponseEntity<PerfilResponse> obterDadosDoMembroAutenticado(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());

        PerfilResponse perfilCompleto = membroUsecase.obterPerfilDoMembroAutenticado(membroId);

        return ResponseEntity.ok(perfilCompleto);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid PerfilRequest dto
    ) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        membroUsecase.atualizarPerfilParcialmente(membroId, dto);
    }*/
}