package br.org.bandasantabarbara.application.dtos.membros;

import java.util.List;

public record VinculosResponse(
        List<VinculoResponse> atuais,
        List<VinculoResponse> encerrados
) { }