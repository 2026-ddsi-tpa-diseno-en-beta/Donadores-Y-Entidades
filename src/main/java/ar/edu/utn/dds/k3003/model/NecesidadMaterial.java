package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "necesidades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_necesidad", discriminatorType = DiscriminatorType.STRING)
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

  @Enumerated(EnumType.STRING)
  @Column
  private TipoNecesidadMaterialEnum tipo;

  @Column
  private Integer cantidadAsignada =0;

  @Column
  private String origenAsignacion;

  @ManyToOne
  @JoinColumn(name = "entidad_id")
  private EntidadBenefica entidadBenefica;


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

  public void satisfacer(Integer cantidadASatisfacer) {
    if (this.cantidadAsignada == null) {
      this.cantidadAsignada = 0;
    }

    this.cantidadAsignada += cantidadASatisfacer;
    if (this.cantidadAsignada > this.cantidadObjetivo) {
      this.cantidadAsignada = this.cantidadObjetivo;
    }
  }
}