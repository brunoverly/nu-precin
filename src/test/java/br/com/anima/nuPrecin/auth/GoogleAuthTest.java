package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GoogleAuthTest {
    private static final String LOGIN = "/v1/auth/google";
    private static final String VINCULAR = LOGIN + "/vincular";
    private static final String BODY = "{\"idToken\":\"token-google\"}";

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private CadastroPendenteRepository cadastros;
    @MockitoBean private GoogleTokenService tokens;

    @Test
    void vinculaContaConfirmadaEMantemIdMesmoQuandoEmailGoogleMuda() throws Exception {
        var usuario = conta("google-teste@gmail.com", true);
        usuario.setFoto("https://example.com/foto-escolhida.jpg");
        usuarios.flush();
        identidade(usuario.getEmail(), true);

        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.token").isNotEmpty());

        identidade("outro@gmail.com", true);
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));

        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/foto-escolhida.jpg",
                usuarios.findById(usuario.getId()).orElseThrow().getFoto());
        usuario.setAtivo(false);
        usuarios.flush();
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void exigeSessaoLocalParaVincularEmailForaDaAutoridadeGoogle() throws Exception {
        var usuario = conta("google-teste@example.org", true);
        identidade(usuario.getEmail(), false);

        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(VINCULAR).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(VINCULAR).with(user(usuario)).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNoContent());
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()));
    }

    @Test
    void exigeVinculoExplicitoQuandoConfirmacaoLocalNaoEstaRegistrada() throws Exception {
        var usuario = conta("google-legado@gmail.com", false);
        identidade(usuario.getEmail(), true);

        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criaContaUmaVezComPerfilGoogleEConsomeCadastroPendente() throws Exception {
        identidade("nova@gmail.com", true);
        var cadastro = cadastros.saveAndFlush(CadastroPendente.builder()
                .nome("Nome pendente").email("nova@gmail.com").senhaHash("senha-pendente")
                .expiraEm(java.time.LocalDateTime.now().plusMinutes(10)).build());
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Google"))
                .andExpect(jsonPath("$.email").value("nova@gmail.com"))
                .andExpect(jsonPath("$.raioBuscaKm").value(5))
                .andExpect(jsonPath("$.token").isNotEmpty());
        var criado = usuarios.findByGoogleSub("google-sub").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(criado.isEmailConfirmado());
        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/perfil.jpg", criado.getFoto());
        org.junit.jupiter.api.Assertions.assertNotEquals("senha-pendente", criado.getSenha());
        org.junit.jupiter.api.Assertions.assertNotNull(cadastros.findById(cadastro.getId()).orElseThrow().getConsumidoEm());
        long total = usuarios.count();
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(criado.getId()));
        org.junit.jupiter.api.Assertions.assertEquals(total, usuarios.count());
    }

    @Test
    void naoRecriaContaDesativadaNemAceitaTokenInvalido() throws Exception {
        var desativado = conta("desativado@gmail.com", true);
        desativado.setAtivo(false);
        usuarios.flush();
        identidade(desativado.getEmail(), true);
        long total = usuarios.count();
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        org.junit.jupiter.api.Assertions.assertEquals(total, usuarios.count());
        when(tokens.verificar("token-google")).thenThrow(
                new br.com.anima.nuPrecin.exception.CredenciaisInvalidasException("Token inválido."));
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        org.junit.jupiter.api.Assertions.assertEquals(total, usuarios.count());
    }

    private Usuario conta(String email, boolean confirmado) {
        return usuarios.saveAndFlush(Usuario.builder().nome("Teste Google").email(email)
                .senha("hash").emailConfirmado(confirmado).build());
    }

    private void identidade(String email, boolean autoridadeGoogle) {
        when(tokens.verificar("token-google"))
                .thenReturn(new GoogleTokenService.Identidade("google-sub", email, autoridadeGoogle, "Maria Google", "https://example.com/perfil.jpg"));
    }
}
