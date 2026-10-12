package br.org.bandasantabarbara.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "file")
public class StoredFile {

    @Id
    private UUID id;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private String filename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    protected StoredFile() {}

    public StoredFile(UUID id, String storageKey, String filename, String contentType) {
        this.id = id;
        this.storageKey = storageKey;
        this.filename = filename;
        this.contentType = contentType;
    }

}