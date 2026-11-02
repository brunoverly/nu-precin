package br.com.anima.nuPrecin.endereco;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EnderecoGeocodingService {
    private final GeoapifyGeocodingClient client;
    private final EnderecoRepository enderecos;

    public EnderecoGeocodingService(GeoapifyGeocodingClient client, EnderecoRepository enderecos) {
        this.client = client;
        this.enderecos = enderecos;
    }

    public void preencherCoordenadas(Endereco endereco) {
        if (!client.isConfigured() || (endereco.getLatitude() != null && endereco.getLongitude() != null)) return;
        endereco.setGeocodificacaoTentadaEm(LocalDateTime.now());
        endereco.setLatitude(null);
        endereco.setLongitude(null);
        client.geocode(endereco).ifPresent(coords -> {
            endereco.setLatitude(coords.latitude());
            endereco.setLongitude(coords.longitude());
        });
    }

    @Transactional
    public void reprocessar(Long id) {
        enderecos.findById(id).ifPresent(endereco -> {
            preencherCoordenadas(endereco);
            enderecos.save(endereco);
        });
    }
}
