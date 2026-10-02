package it.linlo.dashbackend.core.error;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String messageCode, Object... messageArguments) {
        super(HttpStatus.CONFLICT, messageCode, messageArguments);
    }
}
