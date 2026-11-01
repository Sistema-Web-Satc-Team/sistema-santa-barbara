package br.org.bandasantabarbara.repositories.ensino;

import java.util.UUID;

public interface AlunoInstrumentoProjection {
    UUID getAlunoId();
    String getInstrumento();
}
