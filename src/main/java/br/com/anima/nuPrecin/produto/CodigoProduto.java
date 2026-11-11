package br.com.anima.nuPrecin.produto;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

public final class CodigoProduto {
    private static final Pattern GTIN = Pattern.compile("[0-9]{8}|[0-9]{12,14}");
    private static final Pattern DIGITAL_LINK = Pattern.compile("(?:^|/)01/([0-9]{14})(?:/|$)");

    private CodigoProduto() {}

    public static String normalizar(String codigo) {
        if (codigo == null || codigo.isBlank() || codigo.strip().length() > 100) {
            throw new IllegalArgumentException("Informe um código de barras de até 100 caracteres.");
        }
        return codigo.strip();
    }

    public static List<String> variantes(String codigo) {
        if (!GTIN.matcher(codigo).matches()) return List.of(codigo);

        String completo = "0".repeat(14 - codigo.length()) + codigo;
        return List.of(14, 13, 12, 8).stream()
                .filter(tamanho -> completo.substring(0, 14 - tamanho).chars().allMatch(c -> c == '0'))
                .map(tamanho -> completo.substring(14 - tamanho))
                .toList();
    }

    static boolean gtinValido(String codigo) {
        if (!GTIN.matcher(codigo).matches()) return false;
        int soma = 0;
        for (int i = codigo.length() - 1, peso = 1; i >= 0; i--, peso = 4 - peso) {
            soma += (codigo.charAt(i) - '0') * peso;
        }
        return soma % 10 == 0;
    }

    static String codigoDoQr(String conteudo) {
        if (GTIN.matcher(conteudo).matches()) return conteudo;
        URI uri;
        try {
            uri = URI.create(conteudo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("QR Code deve conter um GTIN ou um GS1 Digital Link.");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getPath() == null) {
            throw new IllegalArgumentException("QR Code deve conter um GTIN ou um GS1 Digital Link.");
        }
        var codigo = DIGITAL_LINK.matcher(uri.getPath());
        if (!codigo.find() || !gtinValido(codigo.group(1))) {
            throw new IllegalArgumentException("QR Code sem um GTIN válido no identificador 01.");
        }
        return codigo.group(1);
    }
}
