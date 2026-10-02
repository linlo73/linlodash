package it.linlo.dashbackend.core.error;

import org.springframework.http.HttpStatus;

/** Eccezione di dominio che porta con sé lo status HTTP da restituire. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final Object[] messageArguments;

    public ApiException(HttpStatus status, String messageCode, Object... messageArguments) {
        super(messageCode);
        this.status = status;
        this.messageArguments = messageArguments;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessageCode() {
        return getMessage();
    }

    public Object[] getMessageArguments() {
        return messageArguments.clone();
    }
}
