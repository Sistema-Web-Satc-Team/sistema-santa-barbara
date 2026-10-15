package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {}