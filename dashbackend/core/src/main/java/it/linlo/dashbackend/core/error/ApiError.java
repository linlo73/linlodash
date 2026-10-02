package it.linlo.dashbackend.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.List;

/** Formato unico di errore restituito da tutte le API. */
public record ApiError(Instant timestamp, int status, String error, String message, String path, List<String> details) {

    public static ApiError of(HttpStatusCode status, String message, String path, List<String> details) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        String reason = resolved != null ? resolved.getReasonPhrase() : "Error";
        return new ApiError(Instant.now(), status.value(), reason, message, path, details);
    }
}
