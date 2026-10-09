package br.org.bandasantabarbara.application.dtos.membros.vinculos;

import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.model.Membro;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record MembroVinculosResponse(
        @Schema(
                description = "Identificador único do membro.",
                example = "0199c5d5-7b6f-7abc-8def-123456789abc"
        )
        UUID id,

        @Schema(
                description = "Nome completo do membro.",
                example = "João da Silva"
        )
        String nome,

        @Schema(
                description = "Data de nascimento do membro.",
                example = "15/08/2007"
        )
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @Schema(
                description = "Idade do membro.",
                example = "22"
        )
        int idade,


        @Schema(
                description = "Sexo do membro.",
                example = "MASCULINO"
        )
        Membro.MembroSexoEnum sexo,

        VinculosResponse vinculos
) {}