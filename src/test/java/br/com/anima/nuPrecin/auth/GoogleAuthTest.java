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
    @MockitoBean private GoogleTokenService tokens;

    @Test
    void vinculaContaConfirmadaEMantemIdMesmoQuandoEmailGoogleMuda() throws Exception {
        var usuario = conta("google-teste@gmail.com", true);
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
        identidade("sem-conta@gmail.com", true);
        mvc.perform(post(LOGIN).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    private Usuario conta(String email, boolean confirmado) {
        return usuarios.saveAndFlush(Usuario.builder().nome("Teste Google").email(email)
                .senha("hash").emailConfirmado(confirmado).build());
    }

    private void identidade(String email, boolean autoridadeGoogle) {
        when(tokens.verificar("token-google"))
                .thenReturn(new GoogleTokenService.Identidade("google-sub", email, autoridadeGoogle));
    }
}
