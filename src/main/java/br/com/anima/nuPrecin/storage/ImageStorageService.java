package br.com.anima.nuPrecin.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Autowired
    private StorageService storageService;

    public String upload(
            String resourceType,
            Long resourceId,
            String folder,
            MultipartFile file) {

        validate(file);

        String contentType = file.getContentType();
        String extension = extensionFor(contentType);

        String objectPath = resourceType
                + "/"
                + resourceId
                + "/"
                + folder
                + "/"
                + UUID.randomUUID()
                + extension;

        try {
            return storageService.upload(
                    objectPath,
                    file.getBytes(),
                    contentType
            );
        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "não foi possível ler a imagem"
            );
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("imagem é obrigatória");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "imagem não pode ultrapassar 5 MB"
            );
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "formato inválido; use JPEG, PNG ou WEBP"
            );
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "formato de imagem inválido"
            );
        };
    }
}
