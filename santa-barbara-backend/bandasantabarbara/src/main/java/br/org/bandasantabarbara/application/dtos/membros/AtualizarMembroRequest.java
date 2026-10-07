package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.model.Membro;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AtualizarMembroRequest(
        @Schema(
                description = "Nome completo do membro.",
                example = "João da Silva",
                minLength = 2,
                maxLength = 72
        )
        @Size(min = 2, max = 72,
                message = "O nome do membro deve ter entre 2 e 72 caracteres.")
        String nome,

        @Schema(
                description = "E-mail utilizado para contato e identificação do membro.",
                example = "joao@example.com"
        )
        @Email(message = "O e-mail deve ser válido")
        String email,

        @Schema(
                description = "Telefone para contato.",
                example = "48999999999",
                minLength = 11,
                maxLength = 11,
                pattern = "\\d{11}"
        )
        @Pattern(
                regexp = "\\d{11}",
                message = "O telefone deve conter exatamente 11 dígitos."
        )
        String telefone,

        @Schema(
                description = "Endereço residencial do membro.",
                example = "Rua das Flores, 123"
        )
        @Size(min = 2, max = 200,
                message = "O endereço deve ter entre 2 e 200 caracteres.")
        String endereco,

        @Schema(
                description = "Sexo cadastrado do membro. Valores disponiveis: MASCULINO, FEMININO e NAO_INFORMADO.",
                example = "MASCULINO"
        )
        Membro.MembroSexoEnum sexo,

        @Schema(
                description = "Data de nascimento do membro.",
                example = "15/03/2007",
                type = "string",
                format = "date"
        )
        @Past(message = "A data de nascimento deve ser uma data no passado")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento
) {
}
