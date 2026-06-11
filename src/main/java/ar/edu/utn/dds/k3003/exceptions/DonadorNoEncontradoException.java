package ar.edu.utn.dds.k3003.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DonadorNoEncontradoException extends RuntimeException {
  public DonadorNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
