package br.org.bandasantabarbara.application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegistrarMembroRequest(
        @NotBlank(message = "O nome do membro não pode estar em branco")
        @Size(min = 2, max = 20, message = "O nome do membro deve ter entre 2 e 20 caracteres")
        String nome,

        @Size(min = 2, max = 150, message = "O sobrenome do membro deve ter entre 2 e 150 caracteres")
        String sobrenome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotEmpty(message = "O membro deve possuir pelo menos um papel")
        String[] papeis,

        @Size(min = 2, max = 15, message = "O telefone deve ter pelo menos 11 dígitios.")
        String telefone,

        @Size(min = 2, max = 200, message = "O endereço deve ter pelo menos 2 dígitios.")
        String endereco,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento

) {
}
