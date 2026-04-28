package ar.edu.utn.dds.k3003.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Queja {
  private String id;
  private String donadorID;
  private String donacionID;
  private String descripcion;
  private LocalDate fecha;
}