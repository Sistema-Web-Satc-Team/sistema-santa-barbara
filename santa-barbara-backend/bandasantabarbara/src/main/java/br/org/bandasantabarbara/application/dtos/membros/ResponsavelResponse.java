package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.model.Membro;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record ResponsavelResponse (
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
                description = "Telefone do membro, contendo exatamente 11 dígitos.",
                example = "48999999999"
        )
        String telefone,

        @Schema(
                description = "Endereço residencial do membro.",
                example = "Rua das Flores, 123"
        )
        String endereco,

        @Schema(
                description = "E-mail do membro.",
                example = "joao@example.com"
        )
        String email,

        @Schema(
                description = "Data de nascimento do membro.",
                example = "15/08/2007"
        )
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @Schema(
                description = "Grau de relação.",
                example = "Pai"
        )
        String grauRelacao
) { }
