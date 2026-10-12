package br.org.bandasantabarbara.application.services.storage;


import java.io.InputStream;

public interface StorageService {

    String store(String directory, String filename, InputStream content);

    InputStream load(String path);

    void delete(String path);

    boolean exists(String path);
}