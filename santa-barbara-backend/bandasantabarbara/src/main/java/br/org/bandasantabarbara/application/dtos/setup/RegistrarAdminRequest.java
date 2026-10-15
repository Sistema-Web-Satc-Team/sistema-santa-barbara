package br.org.bandasantabarbara.application.dtos.setup;


import br.org.bandasantabarbara.model.Membro;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Date;


public record RegistrarAdminRequest(

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2,  message = "O nome deve ter no mínino 2 caracteres.")
        @Size(max = 72, message = "Limite de caracteres excedido")
        String nome,

        @NotNull(message = "Data de nascimento precisa ser definida.")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @NotNull(message = "O sexo precisa ser definido.")
        Membro.MembroSexoEnum sexo,

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2, message = "O usuário deve ter no mínimo 2 caracteres")
        @Size(max = 32, message = "Limite de caracteres excedido")
        String nomeUsuario,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {

}
