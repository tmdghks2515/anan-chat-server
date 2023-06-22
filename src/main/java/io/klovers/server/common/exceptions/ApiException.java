package io.klovers.server.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class ApiException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private final boolean showErrMsg = true;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
