package br.com.anima.nuPrecin.endereco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "app.geocoding.backfill-enabled", havingValue = "true", matchIfMissing = true)
public class GeocodingBackfillJob {
    private static final Logger log = LoggerFactory.getLogger(GeocodingBackfillJob.class);
    private final GeoapifyGeocodingClient client;
    private final EnderecoRepository enderecos;
    private final EnderecoGeocodingService service;

    public GeocodingBackfillJob(GeoapifyGeocodingClient client, EnderecoRepository enderecos,
                               EnderecoGeocodingService service) {
        this.client = client;
        this.enderecos = enderecos;
        this.service = service;
    }

    @Scheduled(initialDelay = 15_000, fixedDelay = 3_600_000)
    public void reprocessarPendentes() {
        if (!client.isConfigured()) return;
        var ids = enderecos.buscarPendentes(LocalDateTime.now().minusHours(6), PageRequest.of(0, 25));
        for (Long id : ids) {
            try {
                service.reprocessar(id);
            } catch (RuntimeException ex) {
                // Inclui conflito otimista: uma edição concorrente será processada na próxima execução.
                log.warn("Endereço {} não pôde ser reprocessado; uma nova tentativa será feita depois.", id);
            }
        }
    }
}
