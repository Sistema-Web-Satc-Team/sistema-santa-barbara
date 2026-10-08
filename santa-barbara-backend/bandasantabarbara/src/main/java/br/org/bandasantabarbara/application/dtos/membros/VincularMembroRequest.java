package br.org.bandasantabarbara.application.dtos.membros;

import java.util.UUID;

public record VincularMembroRequest (
    UUID idMembro,
    String funcao
) {}
