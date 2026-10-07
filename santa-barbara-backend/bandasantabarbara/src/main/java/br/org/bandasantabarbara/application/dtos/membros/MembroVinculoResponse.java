package br.org.bandasantabarbara.application.dtos.membros;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record MembroVinculoResponse(

        @Schema(
                description = "Identificador único do membro.",
                example = "0199c5d5-7b6f-7abc-8def-123456789abc"
        )
        UUID idMembro,

        @Schema(
                description = "Nome completo do membro.",
                example = "João da Silva"
        )
        String nome,

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
        FuncaoResponse funcao
) {}