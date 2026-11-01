package br.org.bandasantabarbara.application.dtos.setup;


import com.fasterxml.jackson.annotation.JsonFormat;

import br.org.bandasantabarbara.model.membro.Membro;
import jakarta.validation.constraints.*;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastrar o administrador inicial do sistema.")
public record RegistrarAdminRequest(

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2,  message = "O nome deve ter no mínino 2 caracteres.")
        @Size(max = 72, message = "Limite de caracteres excedido")
        @Schema(description = "Nome completo do administrador.", example = "Maria da Silva", minLength = 2, maxLength = 72, requiredMode = Schema.RequiredMode.REQUIRED)
        String nome,

        @NotNull(message = "Data de nascimento precisa ser definida.")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(description = "Data de nascimento no formato dd/MM/yyyy.", example = "15/05/1985", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate dataNascimento,

        @NotNull(message = "O sexo precisa ser definido.")
        @Schema(description = "Sexo do membro.", example = "FEMININO", requiredMode = Schema.RequiredMode.REQUIRED)
        Membro.MembroSexoEnum sexo,

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2, message = "O usuário deve ter no mínimo 2 caracteres")
        @Size(max = 32, message = "Limite de caracteres excedido")
        @Schema(description = "Nome de usuário para login.", example = "admin", minLength = 2, maxLength = 32, requiredMode = Schema.RequiredMode.REQUIRED)
        String nomeUsuario,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        @Schema(description = "E-mail do administrador.", example = "admin@santabarbara.org", format = "email", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Schema(description = "Senha de acesso.", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String senha
) {

}
