package br.org.bandasantabarbara.application.events;


import java.util.UUID;

public record MembroVinculoCriadoEvent(
    UUID idMembro,
    String codigoFuncao
) {}
