package br.org.bandasantabarbara.application.dtos.membros.vinculos;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Vínculos de um membro agrupados pelo estado.")
public record VinculosResponse(
        @Schema(description = "Vínculos ativos ou agendados.")
        List<VinculoResponse> atuais,
        @Schema(description = "Vínculos encerrados.")
        List<VinculoResponse> encerrados,
        @Schema(description = "Vínculos cancelados.")
        List<VinculoResponse> cancelados
) { }