package br.org.bandasantabarbara.application.dtos.sala;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Contrato para cadastro de sala física.")
public record SalaRequestDTO(

    @NotBlank(message = "O código da sala é obrigatório.")
    @Schema(
        description = "Código identificador da sala.",
        example = "1.10",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String code,

    @NotNull(message = "A capacidade da sala é obrigatória.")
    @Positive(message = "A capacidade da sala deve ser maior que zero.")
    @Schema(
        description = "Capacidade máxima de pessoas permitida na sala.",
        example = "12",
        minimum = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer capacidade

) {}