package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.auth.LoginRequest;
import br.org.bandasantabarbara.application.dtos.auth.TokenResponse;
import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.usecase.auth.AtribuirFotoPerfilUseCase;
import br.org.bandasantabarbara.application.usecase.auth.AutenticarMembroUseCase;
import br.org.bandasantabarbara.application.usecase.auth.PerfilService;
import br.org.bandasantabarbara.application.usecase.auth.RemoverFotoPerfilUseCase;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AutenticarMembroUseCase autenticarMembroUseCase;
    private final PerfilService perfilService;
    private final AtribuirFotoPerfilUseCase atribuirFotoPerfilUseCase;
    private final RemoverFotoPerfilUseCase removerFotoPerfilUseCase;

    public AutenticacaoController(AutenticarMembroUseCase autenticarMembroUseCase, PerfilService perfilService, AtribuirFotoPerfilUseCase atribuirFotoPerfilUseCase, RemoverFotoPerfilUseCase removerFotoPerfilUseCase) {
        this.autenticarMembroUseCase = autenticarMembroUseCase;
        this.perfilService = perfilService;
        this.atribuirFotoPerfilUseCase = atribuirFotoPerfilUseCase;
        this.removerFotoPerfilUseCase = removerFotoPerfilUseCase;
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

    @GetMapping("/me")
    public ResponseEntity<PerfilResponse> obterDadosDoMembroAutenticado(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        String nomeUsuario = jwt.getClaimAsString("nomeUsuario");
        List<String> papeis = jwt.getClaim("papeis");

        PerfilResponse perfilCompleto = perfilService.buscarComClaims(membroId, nomeUsuario, papeis);

        return ResponseEntity.ok(perfilCompleto);
    }

    @PostMapping(value = "/me/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adicionarFotoPerfil(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        this.atribuirFotoPerfilUseCase.execute(membroId, file);
    }

    @DeleteMapping("/me/foto-perfil")
    public ResponseEntity<Void> removerAvatar(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        removerFotoPerfilUseCase.execute(membroId);
        return ResponseEntity.noContent().build();   // 204
    }

    @GetMapping("/me/verificar")
    public boolean verificar(@AuthenticationPrincipal Jwt jwt) {

        return jwt.getSubject() != null;
    }


    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid PerfilRequest dto
    ) {
        UUID membroId = UUID.fromString(jwt.getSubject());

        perfilService.atualizar(membroId, dto);

    }
}