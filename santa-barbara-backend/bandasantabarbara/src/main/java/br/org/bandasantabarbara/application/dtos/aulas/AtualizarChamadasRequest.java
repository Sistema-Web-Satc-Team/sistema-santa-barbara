package br.org.bandasantabarbara.application.dtos.aulas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AtualizarChamadasRequest(
    @NotNull List<@Valid ChamadaRequest> chamadas
) {}
