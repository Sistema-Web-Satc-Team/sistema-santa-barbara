package br.org.bandasantabarbara.application.dtos.membros.vinculos;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record VincularMembroRequest (
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,

    @NotBlank
    String funcao
) {}
