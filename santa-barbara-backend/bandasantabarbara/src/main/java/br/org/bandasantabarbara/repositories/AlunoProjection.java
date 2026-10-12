package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public interface AlunoProjection {
    UUID getId();
    String getNome();
    String getEmail();
    String getTelefone();
    Membro.MembroSexoEnum getSexo();
    LocalDate getDataNascimento();
    LocalDate getDataInicio();
    LocalDate getDataTermino();
    Instant getDeletadoEm();
}