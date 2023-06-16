package io.klovers.server.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
