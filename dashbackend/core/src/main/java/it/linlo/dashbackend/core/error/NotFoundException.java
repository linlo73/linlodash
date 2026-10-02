package it.linlo.dashbackend.core.error;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String messageCode, Object... messageArguments) {
        super(HttpStatus.NOT_FOUND, messageCode, messageArguments);
    }
}
