package br.com.anima.nuPrecin.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageStorageServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ImageStorageService service;

    @Test
    void shouldUploadValidPng() throws IOException {
        MultipartFile file = file("image/png", 1024L, new byte[]{1, 2, 3});
        when(storageService.upload(any(), any(), eq("image/png")))
                .thenReturn("https://storage.example/image.png");

        String url = service.upload("produtos", 1L, "imagens", file);

        assertEquals("https://storage.example/image.png", url);
    }

    @Test
    void shouldRejectImageAboveFiveMegabytes() {
        MultipartFile file = file("image/png", 5 * 1024 * 1024L + 1, new byte[0]);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.upload("produtos", 1L, "imagens", file)
        );

        assertEquals("imagem não pode ultrapassar 5 MB", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedImageType() {
        MultipartFile file = file("application/pdf", 1024L, new byte[]{1});

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.upload("produtos", 1L, "imagens", file)
        );

        assertEquals("formato inválido; use JPEG, PNG ou WEBP", exception.getMessage());
    }

    private MultipartFile file(String contentType, long size, byte[] content) {
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(size);
        lenient().when(file.getContentType()).thenReturn(contentType);
        try {
            lenient().when(file.getBytes()).thenReturn(content);
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
        return file;
    }
}
