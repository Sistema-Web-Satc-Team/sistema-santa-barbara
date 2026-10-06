package br.org.bandasantabarbara.application.dtos.auth;

import java.util.List;

public record GerarTokenRequest (
        String id,
        String email,
        String nomeUsuario,
        List<String> papeis
)
{ }
