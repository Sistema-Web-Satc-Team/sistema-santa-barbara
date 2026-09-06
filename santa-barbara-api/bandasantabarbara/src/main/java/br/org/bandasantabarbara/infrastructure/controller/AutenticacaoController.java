package br.org.bandasantabarbara.infrastructure.controller;


import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.usecase.AutenticarMembroUseCase;
import br.org.bandasantabarbara.application.usecase.MembroUsecase;
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
    private final MembroUsecase membroUsecase;

    public AutenticacaoController(AutenticarMembroUseCase autenticarMembroUseCase, MembroUsecase membroUsecase) {
        this.autenticarMembroUseCase = autenticarMembroUseCase;
        this.membroUsecase = membroUsecase;
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultMessageResponse> login(@RequestBody @Valid LoginRequest request) {
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
                .body(new DefaultMessageResponse("Login realizado com sucesso!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultMessageResponse> logout() {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new DefaultMessageResponse("Logout realizado com sucesso!"));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> obterDadosDoMembroAutenticado(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());

        MeResponse perfilCompleto = membroUsecase.obterPerfilDoMembroAutenticado(membroId);

        return ResponseEntity.ok(perfilCompleto);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AtualizarParcialmenteMembroRequest dto
    ) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        membroUsecase.atualizarParcialmente(membroId, dto);
    }
}