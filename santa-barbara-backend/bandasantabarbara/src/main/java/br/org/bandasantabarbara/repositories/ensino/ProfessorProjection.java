package br.org.bandasantabarbara.repositories.ensino;

import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;

public interface ProfessorProjection {
    UUID getId();
    String getNome();
    String getEmail();
    String getTelefone();
    Membro.MembroSexoEnum getSexo();
}
