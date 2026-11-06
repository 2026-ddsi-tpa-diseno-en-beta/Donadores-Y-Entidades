package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.*;

@Entity
@Table(name = "entregas_recibidas")
public class EntregaRecibida {
  @Id
  @Column(length = 200)
  private String id;

  @Column(nullable = false)
  private String necesidadID;

  @Column(nullable = false)
  private Integer cantidad;

  protected EntregaRecibida() {}

  public EntregaRecibida(String id, String necesidadID, Integer cantidad) {
    this.id = id;
    this.necesidadID = necesidadID;
    this.cantidad = cantidad;
  }

  public boolean coincide(String necesidad, Integer unidades) {
    return necesidadID.equals(necesidad) && cantidad.equals(unidades);
  }
}
