package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.model.*;

public class DonadoresYEntidadesDataMapper {
  
  public DonadorDTO toDonadorDTO(Donador donador) {
    if (donador == null) return null;
    return new DonadorDTO(donador.getId(), donador.getNombre(), donador.getApellido(), 
                          donador.getEdad(), donador.getEmail(), donador.getNroDocumento(), 
                          donador.getDomicilio(), donador.getEstado(), donador.getCategoria());
  }

  public Donador toDonador(DonadorDTO dto) {
    if (dto == null) return null;
    Donador m = new Donador(dto.nombre(), dto.apellido(), dto.edad(), dto.email(), 
                            dto.nroDocumento(), dto.domicilio());
    m.setId(dto.id()); 
    m.setEstado(dto.estado());
    m.setCategoria(dto.categoria());
    return m;
  }

  public EntidadBeneficaDTO toEntidadDTO(EntidadBenefica e) {
    return new EntidadBeneficaDTO(e.getId(), e.getRazonSocial(), e.getDomicilio(), 
                                  e.getTelefono(), e.getCorreo());
  }

  public EntidadBenefica toEntidad(EntidadBeneficaDTO dto) {
    EntidadBenefica e = new EntidadBenefica();
    e.setId(dto.id());
    e.setRazonSocial(dto.razonSocial());
    e.setDomicilio(dto.domicilio());
    return e;
  }

  public NecesidadMaterialDTO toNecesidadDTO(NecesidadMaterial n) {
    return new NecesidadMaterialDTO(n.getId(), n.getEntidadID(), n.getNivelDeUrgencia(), 
                                     n.getDescripcion(), n.getCantidadObjetivo(), 
                                     n.getProductoSolicitadoID(), n.getTipo());
  }

  public NecesidadMaterial toNecesidad(NecesidadMaterialDTO dto) {
    return new NecesidadMaterial(dto.id(), dto.descripcion(), dto.cantidadObjetivo(), 
                                 dto.nivelDeUrgencia(), dto.entidadID(), dto.productoSolicitadoID(), dto.tipo());
  }

  public QuejaDTO toQuejaDTO(Queja q) {
    return new QuejaDTO(q.getId(), q.getDonacionID(), q.getDonadorID(), q.getFecha(), q.getDescripcion());
  }
}