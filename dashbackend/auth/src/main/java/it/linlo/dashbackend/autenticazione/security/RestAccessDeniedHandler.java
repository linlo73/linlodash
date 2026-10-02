package it.linlo.dashbackend.autenticazione.security;

import tools.jackson.databind.ObjectMapper; // CORRETTO per Spring Boot 4!
import it.linlo.dashbackend.core.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.util.List;

/** 403 in formato ApiError per le regole di autorizzazione a livello di filter chain. */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;
    private final MessageSource messages;

    public RestAccessDeniedHandler(ObjectMapper mapper, MessageSource messages) {
        this.mapper = mapper;
        this.messages = messages;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(),
                ApiError.of(HttpStatus.FORBIDDEN,
                        messages.getMessage("error.auth.access-denied", null, request.getLocale()),
                        request.getRequestURI(), List.of()));
    }
}
