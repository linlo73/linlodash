package it.linlo.dashbackend.core.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Locale;

/**
 * Gestione centralizzata degli errori. Estende ResponseEntityExceptionHandler così anche le
 * eccezioni standard di Spring MVC (404, 405, JSON malformato, ...) escono nel formato ApiError.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messages;

    public GlobalExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        String message = messages.getMessage(ex.getMessageCode(), ex.getMessageArguments(), req.getLocale());
        return build(ex.getStatus(), message, req.getRequestURI(), List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + validationMessage(v.getMessageTemplate(), v.getMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, message("error.validation", req.getLocale()),
                req.getRequestURI(), details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violazione di integrità: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, message("error.integrity", req.getLocale()),
                req.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Errore non gestito su {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message("error.internal", req.getLocale()),
                req.getRequestURI(), List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(e -> (e instanceof FieldError f ? f.getField() : e.getObjectName())
                        + ": " + messages.getMessage(e, LocaleContextHolder.getLocale()))
                .toList();
        ApiError body = ApiError.of(status, message("error.validation", LocaleContextHolder.getLocale()),
                path(request), details);
        return ResponseEntity.status(status).headers(headers).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        String message = statusCode.is5xxServerError()
                ? message("error.internal", LocaleContextHolder.getLocale())
                : (body instanceof ProblemDetail pd && pd.getDetail() != null ? pd.getDetail() : ex.getMessage());
        ApiError error = ApiError.of(statusCode, message, path(request), List.of());
        return ResponseEntity.status(statusCode).headers(headers).body(error);
    }

    private static ResponseEntity<ApiError> build(HttpStatus status, String message, String path, List<String> details) {
        return ResponseEntity.status(status).body(ApiError.of(status, message, path, details));
    }

    private static String path(WebRequest request) {
        return request instanceof ServletWebRequest swr ? swr.getRequest().getRequestURI() : "";
    }

    private String validationMessage(String template, String fallback) {
        if (template.startsWith("{") && template.endsWith("}")) {
            return messages.getMessage(template.substring(1, template.length() - 1),
                    null, fallback, LocaleContextHolder.getLocale());
        }
        return fallback;
    }

    private String message(String code, Locale locale) {
        return messages.getMessage(code, null, locale);
    }
}
