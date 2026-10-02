package it.linlo.dashbackend.autenticazione.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "0123456789-0123456789-0123456789-ab";
    private static final String OTHER_SECRET = "zzzzzzzzzz-zzzzzzzzzz-zzzzzzzzzz-zz";

    private final UserDetails mario = User.withUsername("mario").password("x").roles("USER").build();

    @Test
    void generatedTokenIsValidAndContainsUsername() {
        JwtService service = new JwtService(new JwtProperties(SECRET, 5));
        String token = service.generateToken(mario);
        assertThat(service.validateAndGetUsername(token)).contains("mario");
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String token = new JwtService(new JwtProperties(OTHER_SECRET, 5)).generateToken(mario);
        assertThat(new JwtService(new JwtProperties(SECRET, 5)).validateAndGetUsername(token)).isEmpty();
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(new JwtProperties(SECRET, -1));
        assertThat(service.validateAndGetUsername(service.generateToken(mario))).isEmpty();
    }

    @Test
    void garbageTokenIsRejected() {
        JwtService service = new JwtService(new JwtProperties(SECRET, 5));
        assertThat(service.validateAndGetUsername("non.un.jwt")).isEmpty();
    }

    @Test
    void shortSecretFailsFast() {
        assertThatThrownBy(() -> new JwtService(new JwtProperties("troppo-corto", 5)))
                .isInstanceOf(IllegalStateException.class);
    }
}
