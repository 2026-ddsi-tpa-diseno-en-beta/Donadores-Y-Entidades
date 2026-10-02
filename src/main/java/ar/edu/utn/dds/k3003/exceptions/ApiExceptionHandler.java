package ar.edu.utn.dds.k3003.exceptions;

import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final DonadorMetricas metrics;

    public ApiExceptionHandler(DonadorMetricas metrics) {
        this.metrics = metrics;
    }
    public record ErrorResponse(LocalDateTime timestamp, String code, String message, String path) {}

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleMissing(NoSuchElementException ex, HttpServletRequest request) {
        metrics.error();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(LocalDateTime.now(), "NOT_FOUND", ex.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(IllegalArgumentException ex, HttpServletRequest request) {
        metrics.error();
        return ResponseEntity.badRequest().body(new ErrorResponse(LocalDateTime.now(), "INVALID_REQUEST", ex.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRemote(RestClientException ex, HttpServletRequest request) {
        metrics.error();
        org.slf4j.LoggerFactory.getLogger(ApiExceptionHandler.class).error("integracion.error ruta={}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(LocalDateTime.now(), "INTEGRATION_ERROR", "No se pudo completar la comunicación con otro componente", request.getRequestURI()));
    }

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

        org.slf4j.LoggerFactory.getLogger(ApiExceptionHandler.class).error("operacion.error ruta={}", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        "INTERNAL_ERROR",
                        "Error interno del servidor",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
        org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleMalformed(Exception ex, HttpServletRequest request) {
        metrics.error();
        return ResponseEntity.badRequest().body(new ErrorResponse(LocalDateTime.now(), "INVALID_REQUEST",
            "Datos o parámetros inválidos", request.getRequestURI()));
    }
}
