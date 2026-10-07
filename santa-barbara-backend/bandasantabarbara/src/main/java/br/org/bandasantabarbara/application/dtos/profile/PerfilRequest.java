package br.org.bandasantabarbara.application.dtos.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilRequest(

        @Schema(
                description = "Telefone do usuário, contendo exatamente 11 dígitos.",
                example = "48999999999"
        )
        @Pattern(
                regexp = "\\d{11}",
                message = "O telefone deve conter exatamente 11 dígitos."
        )
        String telefone,

        @Schema(
                description = "Endereço residencial do usuário.",
                example = "Rua das Flores, 123"
        )
        @Size(
                min = 2,
                max = 200,
                message = "O endereço deve ter entre 2 e 200 caracteres."
        )
        String endereco,

        @Schema(
                description = "Data de nascimento do usuário.",
                example = "15/08/2007"
        )
        @Past(message = "A data de nascimento deve ser uma data no passado.")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @Schema(
                description = "Identificador do arquivo utilizado como avatar do usuário.",
                example = "0199c5d5-7b6f-7abc-8def-123456789abc",
                nullable = true
        )
        UUID avatarFileId

) {}