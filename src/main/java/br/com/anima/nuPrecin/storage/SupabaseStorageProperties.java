package br.com.anima.nuPrecin.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "supabase")
@Getter
@Setter
public class SupabaseStorageProperties {

    private String url;
    private String storageBucket;
    private String serviceRoleKey;
}