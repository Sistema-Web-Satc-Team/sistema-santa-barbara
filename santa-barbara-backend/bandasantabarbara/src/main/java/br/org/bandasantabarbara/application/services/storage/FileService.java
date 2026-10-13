package br.org.bandasantabarbara.application.services.storage;

import br.org.bandasantabarbara.model.StoredFile;
import br.org.bandasantabarbara.repositories.StoredFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    private final StorageService storage;
    private final StoredFileRepository repository;

    public FileService(StorageService storage, StoredFileRepository repository) {
        this.storage = storage;
        this.repository = repository;
    }

    public StoredFile upload(MultipartFile multipart) {
        if (multipart.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        String contentType = multipart.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido: " + contentType);
        }

        UUID id = UUID.randomUUID();
        String key;
        try (InputStream in = multipart.getInputStream()) {
            key = storage.store("files", id.toString(), in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try {
            return repository.save(new StoredFile(id, key, sanitize(multipart.getOriginalFilename()), contentType));
        } catch (RuntimeException e) {
            storage.delete(key);
            throw e;
        }
    }

    public StoredFile getMetadata(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public InputStream open(StoredFile file) {
        return storage.load(file.getStorageKey());
    }

    @Transactional
    public void delete(UUID id) {
        StoredFile file = getMetadata(id);
        repository.delete(file);
        storage.delete(file.getStorageKey());
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) return "arquivo";
        String clean = Path.of(name).getFileName().toString();
        return clean.length() > 128 ? clean.substring(clean.length() - 128) : clean;
    }


    public String urlFor(UUID fileId) {
        if (fileId == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/{id}")
                .buildAndExpand(fileId)
                .toUriString();
    }

    public String urlForByResource(String resource, UUID fileId) {
        if (fileId == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(resource + "/files/{id}")
                .buildAndExpand(fileId)
                .toUriString();
    }

    public String urlForByResource(String resource, StoredFile file) {
        return file == null ? null : urlForByResource(resource, file.getId());
    }

    public String urlFor(StoredFile file) {
        return file == null ? null : urlFor(file.getId());
    }
}