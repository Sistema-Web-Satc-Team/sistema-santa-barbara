package br.org.bandasantabarbara.repositories.ensino;

import java.time.LocalDate;
import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;

public interface AlunoProjection {
    UUID getId();
    String getNome();
    String getEmail();
    String getTelefone();
    Membro.MembroSexoEnum getSexo();
    LocalDate getDataNascimento();
}