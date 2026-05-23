package com.eva2.staem.exception;

import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<?> handleTimeout(WebClientRequestException ex) {
        log.error("Tiempo de espera agotado al llamar al servicio remoto", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "Gateway Timeout");
        body.put("message", "No se pudo conectar con el servicio remoto (timeout)");
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("Excepción no controlada", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage() == null ? "Error interno" : ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
