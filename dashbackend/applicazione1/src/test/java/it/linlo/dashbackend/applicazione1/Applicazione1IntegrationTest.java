package it.linlo.dashbackend.applicazione1;

// CORRETTO per Spring Boot 4!

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Applicazione1Application.class)
@ActiveProfiles("test") // Carica il file application-test.yml
@AutoConfigureMockMvc
class Applicazione1IntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired
    JsonMapper mapper;

    @Test
    void protectedEndpointWithoutTokenReturns401InApiErrorFormat() throws Exception {
        mvc.perform(get("/api/app1/hello"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/app1/hello"));
    }

    @Test
    void registerThenCallProtectedEndpointWithToken() throws Exception {
        String token = register("mario1", "mario1@example.com", "password123");
        mvc.perform(get("/api/app1/hello").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ciao mario1"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        register("luigi1", "luigi1@example.com", "password123");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "luigi1", "password", "sbagliata"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenziali non valide"));
    }

    @Test
    void invalidRegistrationReturns400WithDetails() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "x", "email", "non-una-email", "password", "corta"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Errore di validazione"))
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void duplicateRegistrationReturns409() throws Exception {
        register("anna1", "anna1@example.com", "password123");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "anna1", "email", "altra1@example.com", "password", "password123"))))
                .andExpect(status().isConflict());
    }

    @Test
    void normalUserCannotAccessAdminEndpoint() throws Exception {
        String token = register("carlo1", "carlo1@example.com", "password123");
        mvc.perform(get("/api/app1/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void unknownPathReturns404InApiErrorFormat() throws Exception {
        String token = register("giulia1", "giulia1@example.com", "password123");
        mvc.perform(get("/api/app1/inesistente").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private String register(String username, String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", username, "email", email, "password", password))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    private String json(Object o) throws Exception {
        return mapper.writeValueAsString(o);
    }
}
