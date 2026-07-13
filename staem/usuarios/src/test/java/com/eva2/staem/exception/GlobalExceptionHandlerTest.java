package com.eva2.staem.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar Timeout Exception")
    void handleTimeout() {
        WebClientRequestException ex = mock(WebClientRequestException.class);
        
        ResponseEntity<?> response = exceptionHandler.handleTimeout(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Gateway Timeout");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar Validation Exception")
    void handleValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "campo", "Mensaje de error");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getMessage()).thenReturn("Validation failed");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("error")).isEqualTo("Validacion fallida");
        Map<String, String> detalle = (Map<String, String>) body.get("detalle");
        assertThat(detalle.get("campo")).isEqualTo("Mensaje de error");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar ResourceNotFoundException")
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        
        ResponseEntity<?> response = exceptionHandler.handleResourceNotFound(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Recurso no encontrado");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar BusinessRuleException")
    void handleBusinessRule() {
        BusinessRuleException ex = new BusinessRuleException("Regla de negocio violada");
        
        ResponseEntity<?> response = exceptionHandler.handleBusinessRule(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Business Rule Violation");
        assertThat(body.get("message")).isEqualTo("Regla de negocio violada");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar Excepcion Generica")
    void handleGeneric() {
        Exception ex = new Exception("Error inesperado");
        
        ResponseEntity<?> response = exceptionHandler.handleGeneric(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("Error inesperado");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe manejar Excepcion Generica nula")
    void handleGeneric_nullMessage() {
        Exception ex = new Exception((String) null);
        
        ResponseEntity<?> response = exceptionHandler.handleGeneric(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Error interno");
    }
}
