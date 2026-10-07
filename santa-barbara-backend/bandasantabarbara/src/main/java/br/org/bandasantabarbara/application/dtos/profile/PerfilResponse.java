package br.org.bandasantabarbara.application.dtos.profile;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PerfilResponse(

        @Schema(
                description = "Identificador único do membro.",
                example = "0199c5d5-7b6f-7abc-8def-123456789abc"
        )
        UUID id,

        @Schema(
                description = "Nome completo do usuário.",
                example = "João da Silva"
        )
        String nome,

        @Schema(
                description = "Nome de usuário utilizado para autenticação.",
                example = "joao.silva"
        )
        String nomeUsuario,

        @Schema(
                description = "E-mail do usuário.",
                example = "joao@example.com"
        )
        String email,

        @Schema(
                description = "Telefone do usuário.",
                example = "48999999999"
        )
        String telefone,

        @Schema(
                description = "Endereço residencial do usuário.",
                example = "Rua das Flores, 123"
        )
        String endereco,

        @Schema(
                description = "Funções atualmente vinculadas ao usuário.",
                example = "[\"MAESTRO\", \"MUSICO_BANDA\"]"
        )
        List<String> funcoes,

        @Schema(
                description = "Data de nascimento do usuário.",
                example = "15/08/2007"
        )
        LocalDate dataNascimento,

        @Schema(
                description = "Identificador do arquivo utilizado como avatar do usuário.",
                example = "0199c5d5-7b6f-7abc-8def-123456789abc",
                nullable = true
        )
        UUID avatarFileId

) {}