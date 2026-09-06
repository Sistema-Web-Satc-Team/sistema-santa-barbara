package br.org.bandasantabarbara.application;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAdminDTO(
        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2, max = 20, message = "O usuário deve ter entre 2 e 20 caracteres")
        String username,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {

}
