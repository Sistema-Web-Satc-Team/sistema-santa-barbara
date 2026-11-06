package br.org.bandasantabarbara.repositories.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import br.org.bandasantabarbara.model.storage.StoredFile;

import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {}