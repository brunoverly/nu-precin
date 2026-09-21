package br.com.anima.nuPrecin.db;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MvpSeedPasswordTest {

    @Test
    void shouldMatchTheMvpAdminPassword() {
        String hash = "$2a$12$eFaR4ZY1O.BMk/mDmlSOj.YaTKt/Fcxw15zR6lRXfjkrUX9f0.KlK";

        assertTrue(new BCryptPasswordEncoder().matches("123", hash));
    }
}
