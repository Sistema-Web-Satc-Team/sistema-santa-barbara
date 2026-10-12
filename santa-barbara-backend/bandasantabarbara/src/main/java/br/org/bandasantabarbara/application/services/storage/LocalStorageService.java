package br.org.bandasantabarbara.application.services.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService implements StorageService {

    private final Path root;

    public LocalStorageService(@Value("${storage.local.root:./uploads}") String rootDir) {
        this.root = Path.of(rootDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar o diretório de storage", e);
        }
    }

    @Override
    public String store(String directory, String filename, InputStream content) {
        Path target = resolve(directory + "/" + filename);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return root.relativize(target).toString().replace('\\', '/');
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar arquivo: " + filename, e);
        }
    }

    @Override
    public InputStream load(String path) {
        try {
            return Files.newInputStream(resolve(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler arquivo: " + path, e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(resolve(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover arquivo: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(resolve(path));
    }

    /** Resolve o caminho e impede path traversal (ex.: "../../etc/passwd"). */
    private Path resolve(String path) {
        Path resolved = root.resolve(path).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Caminho inválido: " + path);
        }
        return resolved;
    }
}