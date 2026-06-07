package ar.edu.utn.dds.k3003.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Queja {
  @Id
  private String id;

  @Column
  private String donadorID;

  @Column
  private String donacionID;

  @Column
  private String descripcion;

  @Column
  private LocalDate fecha;
}

