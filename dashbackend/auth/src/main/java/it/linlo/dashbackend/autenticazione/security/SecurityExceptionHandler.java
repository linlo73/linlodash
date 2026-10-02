package it.linlo.dashbackend.autenticazione.security;

import it.linlo.dashbackend.core.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.MessageSource;

import java.util.List;

/**
 * Gestisce le eccezioni di sicurezza lanciate dentro i controller (login fallito, @PreAuthorize).
 * Precedenza massima, altrimenti il gestore generico di core le trasformerebbe in 500.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {

    private final MessageSource messages;

    public SecurityExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.of(HttpStatus.UNAUTHORIZED,
                        messages.getMessage("error.auth.credentials", null, req.getLocale()),
                        req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiError.of(HttpStatus.FORBIDDEN,
                        messages.getMessage("error.auth.access-denied", null, req.getLocale()),
                        req.getRequestURI(), List.of()));
    }
}
