package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Donador {
  private String id;
  private String nombre;
  private String apellido;
  private Integer edad;
  private String email;
  private String nroDocumento;
  private String domicilio;
  private EstadoDonadorEnum estado;
  private String categoria;
  private List<Queja> listaDeQuejas = new ArrayList<>();
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