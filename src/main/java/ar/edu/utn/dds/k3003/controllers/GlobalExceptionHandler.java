package ar.edu.utn.dds.k3003.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleBusinessRules(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "";

        if (mensaje.contains("parciales para necesidades recurrentes")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
        }

        if (mensaje.contains("producto solicitado no válido")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
        }

        if (mensaje.contains("ya existe") || mensaje.contains("duplicada") || mensaje.contains("inválido")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensaje);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + mensaje);
    }
}