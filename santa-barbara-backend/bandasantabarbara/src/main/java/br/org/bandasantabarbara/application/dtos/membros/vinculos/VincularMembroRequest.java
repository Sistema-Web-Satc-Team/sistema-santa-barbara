package br.org.bandasantabarbara.application.dtos.membros.vinculos;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para vincular uma função a um membro.")
public record VincularMembroRequest (
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(description = "Data de início do vínculo no formato dd/MM/yyyy.", example = "01/01/2027", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate dataInicio,

    @NotBlank
    @Schema(description = "Código ou nome da função a atribuir.", example = "ALUNO", requiredMode = Schema.RequiredMode.REQUIRED)
    String funcao
) {}
