package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.auth.LoginRequest;
import br.org.bandasantabarbara.application.dtos.auth.TokenResponse;
import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.services.membro.AuthService;
import br.org.bandasantabarbara.application.services.membro.PerfilService;
import br.org.bandasantabarbara.application.services.storage.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login, sessão e gerenciamento do perfil autenticado.")
public class AutenticacaoController {

    private final AuthService authService;
    private final PerfilService perfilService;
    private final FileService fileService;

    @Value("${app.security.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.security.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.security.cookie.path}")
    private String cookiePath;

    public AutenticacaoController(AuthService authService, FileService fileService, PerfilService perfilService) {
        this.authService = authService;
        this.perfilService = perfilService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica as credenciais e define o cookie de acesso.")
    public ResponseEntity<MessageResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = authService.executar(request);

        ResponseCookie cookie = ResponseCookie
                .from("access_token", response.token())
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(response.expiraEmEmSegundos())
                .sameSite(cookieSameSite)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(
                    new MessageResponse(
                        "Login realizado com sucesso!"
                    )
                );
    }

    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Remove o cookie de acesso da sessão atual.")
    public ResponseEntity<MessageResponse> logout() {
        ResponseCookie cookie = ResponseCookie
            .from("access_token", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .maxAge(0)
            .sameSite(cookieSameSite)
            .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logout realizado com sucesso!"));
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar perfil autenticado", description = "Retorna os dados do membro associado ao token atual.")
    public ResponseEntity<PerfilResponse> obterDadosDoMembroAutenticado(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        String nomeUsuario = jwt.getClaimAsString("nomeUsuario");
        List<String> papeis = jwt.getClaim("papeis");

        PerfilResponse perfilCompleto = perfilService.buscarComClaims(membroId, nomeUsuario, papeis);

        return ResponseEntity.ok(perfilCompleto);
    }

    @PostMapping(value = "/me/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Adicionar foto de perfil", description = "Envia uma imagem para ser usada como foto do membro autenticado.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adicionarFotoPerfil(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        this.perfilService.adicionarFotoPerfil(membroId, file);
    }

    @DeleteMapping("/me/foto-perfil")
    @Operation(summary = "Remover foto de perfil", description = "Remove a foto do membro autenticado.")
    public ResponseEntity<Void> removerAvatar(@AuthenticationPrincipal Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        perfilService.removerFotoPerfil(membroId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/verificar")
    @Operation(summary = "Verificar sessão", description = "Confirma que há um token autenticado válido.")
    public boolean verificar(@AuthenticationPrincipal Jwt jwt) {

        return jwt.getSubject() != null;
    }

    @GetMapping("/me/files/{id}")
    @Operation(summary = "Baixar foto de perfil", description = "Retorna a foto de perfil do membro autenticado.")
    public ResponseEntity<Resource> download(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        var fileMetadata = this.fileService.getMetadata(id);
        var inputStream = this.perfilService.verFotoPerfil(id, UUID.fromString(jwt.getSubject()));

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(fileMetadata.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new InputStreamResource(inputStream));

    }


    @PatchMapping("/me")
    @Operation(summary = "Atualizar perfil", description = "Atualiza os dados editáveis do membro autenticado.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid PerfilRequest dto
    ) {
        UUID membroId = UUID.fromString(jwt.getSubject());

        perfilService.atualizar(membroId, dto);

    }
}