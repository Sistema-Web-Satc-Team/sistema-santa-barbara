package br.org.bandasantabarbara.unit;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.application.services.storage.StorageService;
import br.org.bandasantabarbara.repositories.storage.StoredFileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileServiceTest {

    @Test
    void removesStoredBytesWhenMetadataPersistenceFails() {
        StorageService storage = mock(StorageService.class);
        StoredFileRepository repository = mock(StoredFileRepository.class);
        RuntimeException persistenceFailure = new RuntimeException("Simulated metadata persistence failure");
        when(storage.store(any(), any(), any())).thenReturn("files/test-id");
        when(repository.save(any())).thenThrow(persistenceFailure);
        FileService service = new FileService(storage, repository);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        assertThrows(RuntimeException.class, () -> service.upload(file));

        verify(storage).delete("files/test-id");
    }
}
