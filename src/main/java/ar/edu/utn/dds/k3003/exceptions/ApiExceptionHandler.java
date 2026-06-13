package ar.edu.utn.dds.k3003.exceptions;

import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final DonadorMetricas metrics;

    public ApiExceptionHandler(DonadorMetricas metrics) {
        this.metrics = metrics;
    }
    public record ErrorResponse(LocalDateTime timestamp, String code, String message, String path) {}

    @ExceptionHandler(DonadorNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DonadorNoEncontradoException ex, HttpServletRequest request) {
        metrics.error();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(LocalDateTime.now(), "DONADOR_NOT_FOUND", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        metrics.error();
        String errorMessage = (ex.getMessage() != null) ? ex.getMessage() : "Error inesperado: " + ex.getClass().getSimpleName();

        // Imprimimos el stack trace en la consola de IntelliJ para que puedas ver el error real
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        "INTERNAL_ERROR",
                        errorMessage,
                        request.getRequestURI()
                ));
    }
}