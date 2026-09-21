package br.com.anima.nuPrecin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "NuPrecin API",
                version = "1.0.0",
                description = """
                        API REST do NuPrecin para cadastro, consulta e comparação de preços.

                        ## Convenções gerais

                        - Todas as rotas de negócio usam o prefixo `/v1`.
                        - As rotas de autenticação (`/v1/auth/**`) são públicas.
                        - As demais rotas exigem `Authorization: Bearer <token>`.
                        - Respostas de erro seguem o objeto `ErrorResponse`.
                        - Exclusões são lógicas: o registro fica com `ativo=false`.
                        - Usuários comuns só podem operar nos próprios recursos; administradores podem gerenciar usuários e recursos em nome de outros usuários.

                        ## Fluxo de cadastro de conta

                        1. `POST /v1/auth/registro` envia um código de 4 dígitos por e-mail e cria um cadastro pendente.
                        2. O frontend solicita o código ao usuário.
                        3. `POST /v1/auth/registro/confirmar` confirma o código e cria a conta definitiva.
                        4. `POST /v1/auth/login` retorna o JWT.
                        5. Em caso de expiração ou perda do código, use `POST /v1/auth/registro/reenviar`.

                        A foto não é obrigatória no cadastro. Depois do login, use `POST /v1/usuarios/{id}/foto` para enviar um arquivo.

                        ## Fluxo de recuperação de senha

                        1. `POST /v1/auth/senha/esqueci` solicita o código para o e-mail.
                        2. O usuário informa o código recebido.
                        3. `POST /v1/auth/senha/resetar` grava a nova senha e retorna `204 No Content`.

                        O endpoint de solicitação sempre retorna uma mensagem genérica para não revelar se um e-mail existe.

                        ## Fluxo de imagens

                        Produtos, estabelecimentos e usuários podem receber uma URL de imagem diretamente no JSON quando o sistema externo já possui uma imagem.

                        Para enviar um arquivo ao Supabase Storage:

                        1. Crie o recurso e obtenha o `id`.
                        2. Envie `multipart/form-data` para o endpoint de imagem correspondente.
                        3. Use o campo de arquivo `file`.
                        4. A API salva o arquivo, atualiza o campo da entidade e retorna a URL pública.

                        Endpoints de arquivo:

                        - Produto: `POST /v1/produtos/{id}/imagem` → campo JSON `imagem`.
                        - Estabelecimento: `POST /v1/estabelecimentos/{id}/foto` → campo JSON `foto`.
                        - Usuário: `POST /v1/usuarios/{id}/foto` → campo JSON `foto`.

                        Formatos aceitos: JPEG, PNG e WEBP. Tamanho máximo: 5 MB. Não defina manualmente o header `Content-Type` no multipart; o cliente deve gerar o boundary.

                        ## Fluxo de produto e estabelecimento

                        1. Crie ou consulte o usuário autenticado.
                        2. Crie o produto e o estabelecimento usando JSON.
                        3. Envie as imagens opcionalmente pelos endpoints multipart.
                        4. Crie uma promoção relacionando o código de barras do produto ao estabelecimento.
                        5. Adicione a promoção ao carrinho.
                        """,
                contact = @Contact(name = "Time NuPrecin"),
                license = @License(name = "Uso interno do projeto")
        ),
        servers = {
                @Server(url = "https://nu-precin.onrender.com", description = "Produção"),
                @Server(url = "http://localhost:8080", description = "Desenvolvimento local")
        },
        tags = {
                @Tag(name = "Autenticação", description = "Login, cadastro, confirmação de e-mail e recuperação de senha."),
                @Tag(name = "Usuários", description = "Conta, perfil, senha e foto do usuário."),
                @Tag(name = "Estabelecimentos", description = "Mercados, supermercados, atacadistas e suas fotos."),
                @Tag(name = "Produtos", description = "Catálogo de produtos, códigos e imagens."),
                @Tag(name = "Promoções", description = "Preços promocionais vinculados a produtos e estabelecimentos."),
                @Tag(name = "Carrinhos", description = "Carrinho único por usuário e itens baseados em promoções."),
                @Tag(name = "Votos", description = "Votos, filtros e ranking de promoções.")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT retornado por POST /v1/auth/login. Envie no formato: Bearer <token>.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
