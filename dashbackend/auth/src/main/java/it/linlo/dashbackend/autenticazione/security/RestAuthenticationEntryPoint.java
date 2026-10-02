package it.linlo.dashbackend.autenticazione.security;

import tools.jackson.databind.ObjectMapper; // CORRETTO per Spring Boot 4!
import it.linlo.dashbackend.core.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.util.List;

/** 401 in formato ApiError per le richieste senza token o con token non valido. */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    private final MessageSource messages;

    public RestAuthenticationEntryPoint(ObjectMapper mapper, MessageSource messages) {
        this.mapper = mapper;
        this.messages = messages;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(),
                ApiError.of(HttpStatus.UNAUTHORIZED,
                        messages.getMessage("error.auth.unauthenticated", null, request.getLocale()),
                        request.getRequestURI(), List.of()));
    }
}
