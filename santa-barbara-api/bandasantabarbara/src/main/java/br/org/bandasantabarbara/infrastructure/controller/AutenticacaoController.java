package br.org.bandasantabarbara.infrastructure.controller;


import br.org.bandasantabarbara.application.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AutenticarMembroUseCase autenticarMembroUseCase;

    public AutenticacaoController(AutenticarMembroUseCase autenticarMembroUseCase) {
        this.autenticarMembroUseCase = autenticarMembroUseCase;
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
        String id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String nomeUsuario = jwt.getClaimAsString("nomeUsuario");

        return ResponseEntity.ok(new MeResponse(id, nomeUsuario, email));
    }
}