package br.com.anima.nuPrecin.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeFrontendContractAndJwtSecurityScheme() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("NuPrecin API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/v1/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/v1/auth/login'].post.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/v1/auth/registro/confirmar']").exists())
                .andExpect(jsonPath("$.paths['/v1/auth/senha/esqueci']").exists())
                .andExpect(jsonPath("$.paths['/v1/produtos'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/v1/produtos/{id}/imagem']").exists())
                .andExpect(jsonPath("$.paths['/v1/estabelecimentos/{id}/foto']").exists())
                .andExpect(jsonPath("$.paths['/v1/usuarios/{id}/foto']").exists());
    }
}
