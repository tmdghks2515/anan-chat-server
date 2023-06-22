package io.klovers.server.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> ApiExceptionHandler(ApiException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put( "message", exception.getMessage());
        body.put( "showErrMsg", exception.isShowErrMsg());

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }

}
