package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Donador {
  @Id
  private String id;

  @Column
  private String nombre;

  @Column
  private String apellido;

  @Column
  private Integer edad;

  @Column
  private String email;

  @Column
  private String nroDocumento;

  @Column
  private String domicilio;

  @Enumerated(EnumType.STRING)
  @Column
  private EstadoDonadorEnum estado;

  @Column
  private String categoria;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "donador_id")
  private List<Queja> listaDeQuejas = new ArrayList<>();

  @ElementCollection
  @Enumerated(EnumType.STRING)
  private List<EstadoDonadorEnum> historialEstados = new ArrayList<>();

  public Donador(String nombre, String apellido, Integer edad, String email, String nroDocumento, String domicilio) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;
    this.email = email;
    this.nroDocumento = nroDocumento;
    this.domicilio = domicilio;
    this.estado = EstadoDonadorEnum.VERIFICADO;
    this.historialEstados.add(this.estado);
  }

  public void registrarQueja(Queja queja) {
    this.listaDeQuejas.add(queja);
  }

  public Boolean puedeHacerDonacion() {
    return !EstadoDonadorEnum.BANEADO.equals(this.estado);
  }
}