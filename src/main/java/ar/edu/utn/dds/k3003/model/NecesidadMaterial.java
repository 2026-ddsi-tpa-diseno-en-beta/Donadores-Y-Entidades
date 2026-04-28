package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NecesidadMaterial {
  private String id;
  private String descripcion;
  private Integer cantidadObjetivo;
  private Integer nivelDeUrgencia;
  private String entidadID;
  private String productoSolicitadoID;
  private TipoNecesidadMaterialEnum tipo;

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