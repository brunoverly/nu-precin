package br.com.anima.nuPrecin.usuario.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;

@Schema(name = "PreferenciasUsuario", description = "Preferência de distância do usuário. Aceita 1, 3, 5 ou 10 km.")
public record PreferenciasUsuarioDto(
        @NotNull(message = "raioBuscaKm é obrigatório")
        @Schema(description = "Distância preferida em quilômetros", example = "3", allowableValues = {"1", "3", "5", "10"}, defaultValue = "5")
        @JsonDeserialize(using = InteiroDeserializer.class)
        Integer raioBuscaKm
) {
    // Evita a conversão padrão do Jackson que truncaria 3.5 para 3.
    public static class InteiroDeserializer extends StdDeserializer<Integer> {
        public InteiroDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return (Integer) context.handleUnexpectedToken(Integer.class, parser);
            }
            return parser.getIntValue();
        }
    }
}
