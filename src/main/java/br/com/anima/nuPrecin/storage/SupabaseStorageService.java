package br.com.anima.nuPrecin.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class SupabaseStorageService implements StorageService {

    private final RestClient restClient = RestClient.create();

    @Autowired
    private SupabaseStorageProperties properties;

    @Override
    public String upload(String objectPath, byte[] content, String contentType) {
        validateConfiguration();
        validateObjectPath(objectPath);

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Conteúdo do arquivo não pode ser vazio.");
        }

        if (!StringUtils.hasText(contentType)) {
            throw new IllegalArgumentException("Tipo do arquivo é obrigatório.");
        }

        restClient.post()
                .uri(buildObjectUrl(objectPath, false))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                        properties.getServiceRoleKey())
                .header("apikey", properties.getServiceRoleKey())
                .header("x-upsert", "false")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content)
                .retrieve()
                .toBodilessEntity();

        return publicUrl(objectPath);
    }

    @Override
    public void delete(String objectPath) {
        validateConfiguration();
        validateObjectPath(objectPath);

        restClient.delete()
                .uri(buildObjectUrl(objectPath, false))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                        properties.getServiceRoleKey())
                .header("apikey", properties.getServiceRoleKey())
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public String publicUrl(String objectPath) {
        validateConfiguration();
        validateObjectPath(objectPath);

        return buildObjectUrl(objectPath, true);
    }

    private String buildObjectUrl(String objectPath, boolean publicObject) {
        String baseUrl = properties.getUrl().replaceAll("/+$", "");
        String visibilityPath = publicObject
                ? "/storage/v1/object/public/"
                : "/storage/v1/object/";

        return baseUrl
                + visibilityPath
                + properties.getStorageBucket()
                + "/"
                + encodePath(objectPath);
    }

    private String encodePath(String objectPath) {
        return Arrays.stream(objectPath.split("/"))
                .map(part -> UriUtils.encodePathSegment(part,
                        StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getUrl())
                || !StringUtils.hasText(properties.getStorageBucket())
                || !StringUtils.hasText(properties.getServiceRoleKey())) {
            throw new IllegalStateException(
                    "Configuração do Supabase Storage não foi preenchida."
            );
        }
    }

    private void validateObjectPath(String objectPath) {
        if (!StringUtils.hasText(objectPath)
                || objectPath.startsWith("/")
                || objectPath.contains("..")) {
            throw new IllegalArgumentException("Caminho de objeto inválido.");
        }
    }
}