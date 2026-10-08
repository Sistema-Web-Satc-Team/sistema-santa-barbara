package br.org.bandasantabarbara.application.dtos.membros.vinculos;

import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.model.MembroVinculo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record VinculoResponse(

        @Schema(
                description = "Identificador único do vínculo.",
                example = "2"
        )
        int id,

        @Schema(
                description = "Data e hora em que o vínculo foi iniciado.",
                example = "2026-10-06T14:30:00Z"
        )
        Instant dataInicio,

        @Schema(
                description = "Data e hora em que o vínculo foi encerrado. Será nulo enquanto o vínculo estiver ativo.",
                example = "2027-03-15T18:45:00Z",
                nullable = true
        )
        Instant dataTermino,

        @Schema(
                description = "Função exercida pelo membro durante o vínculo."
        )
        FuncaoResponse funcao,


        @Schema(
                description = "Status atual do vínculo. pode ser ativo ou encerrado."
        )
        MembroVinculo.MembroVinculoStatusEnum status
) {}