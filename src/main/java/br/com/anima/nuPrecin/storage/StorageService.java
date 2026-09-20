package br.com.anima.nuPrecin.storage;

public interface StorageService {
    String upload(String objectPath, byte[] content, String contentType);
    void delete(String objectPath);
    String publicUrl(String objectPath);
}
