package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class NecesidadMaterial {
  @Id
  private String id;

  @Column
  private String descripcion;

  @Column
  private Integer cantidadObjetivo;
  
  @Column
  private Integer nivelDeUrgencia;

  @Column
  private String entidadID;

  @Column
  private String productoSolicitadoID;

  @Enumerated(EnumType.STRING) //base que guarda una palabra y no un número
  @Column
  private TipoNecesidadMaterialEnum tipo;

  @Column
  private Integer cantidadAsignada;

  @Column
  private String origenAsignacion;

  public void setCantidadAsignada(Integer cantidadAsignada) {
    this.cantidadAsignada = cantidadAsignada;
  }

  public void setOrigenAsignacion(String origenAsignacion) {
    this.origenAsignacion = origenAsignacion;
  }

  public NecesidadMaterial(String id, String descripcion, Integer cantidadObjetivo, 
      Integer nivelDeUrgencia, String entidadID, String productoSolicitadoID, TipoNecesidadMaterialEnum tipo) 
    {
    this.id = id;
    this.descripcion = descripcion;
    this.cantidadObjetivo = cantidadObjetivo;
    this.nivelDeUrgencia = nivelDeUrgencia;
    this.entidadID = entidadID;
    this.productoSolicitadoID = productoSolicitadoID;
    this.tipo = tipo;
  }
}