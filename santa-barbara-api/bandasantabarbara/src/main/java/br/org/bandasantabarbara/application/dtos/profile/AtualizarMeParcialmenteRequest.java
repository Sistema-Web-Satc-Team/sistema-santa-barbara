package br.org.bandasantabarbara.application.dtos.profile;
import jakarta.validation.constraints.*;

public record AtualizarMeParcialmenteRequest(
    @Size(min = 2, max = 20, message = "O nome deve ter entre 2 e 20 caracteres")
    String nomeDeUsuario,

    @Email(message = "O e-mail deve ser válido")
    String email,

    @Size(min = 11, max = 15, message = "O telefone deve ter pelo menos 11 dígitios.")
    String telefone,

    @Size(min = 2, max = 200, message = "O endereço deve ter pelo menos 2 dígitios.")
    String endereco
) {}