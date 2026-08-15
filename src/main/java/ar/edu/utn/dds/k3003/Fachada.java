package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.*;
import java.util.*;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;



import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class Fachada implements FachadaDonadoresYEntidades {

  
  @Autowired
  private DonadoresRepository donadoresRepository;
  private EntidadesRepository entidadesRepository;
  private NecesidadMaterialRepository necesidadMaterialRepository;
  
  private DonadoresYEntidadesDataMapper dataMapper = new DonadoresYEntidadesDataMapper();
  @Autowired
  private FachadaIncentivos fachadaIncentivos;
  @Autowired
  private FachadaDonaciones fachadaDonaciones;
  @Autowired
  private FachadaLogistica fachadaLogistica;
  private int idCounter = 1;

  @Autowired
  public Fachada(DonadoresRepository donadoresRepository,
                 EntidadesRepository entidadesRepository,
                 NecesidadMaterialRepository necesidadMaterialRepository) {
    this.donadoresRepository = donadoresRepository;
    this.entidadesRepository = entidadesRepository;
    this.necesidadMaterialRepository = necesidadMaterialRepository;
  }

  @Autowired
  private DonadorMetricas metrics;
  public Fachada() {
  }


  public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) { this.fachadaDonaciones = fachadaDonaciones;}
  public void setFachadaLogistica(FachadaLogistica fachadaLogistica) { this.fachadaLogistica = fachadaLogistica; }
  @Override
  public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {
    this.fachadaIncentivos = fachadaIncentivos;
  }



  @Override
  public DonadorDTO agregarDonador(@Valid @RequestBody DonadorDTO donadorDTO) {
    if (donadorDTO == null) throw new RuntimeException();
    //genero id si no se le asignó
    String id = (donadorDTO.id() == null || donadorDTO.id().isBlank())
            ? java.util.UUID.randomUUID().toString()
            : donadorDTO.id();
    if (donadoresRepository.findById(id).isPresent()) {
        throw new NoSuchElementException("Error: Ya existe un donador con el ID: " + donadorDTO.id());
    }

    Donador donadorModel = dataMapper.toDonador(donadorDTO);
    donadorModel.setId(id);
    donadorModel.setEstado(EstadoDonadorEnum.VERIFICADO);
    donadoresRepository.save(donadorModel);
    metrics.donadorRegistrado();
    return dataMapper.toDonadorDTO(donadorModel);
  }

  @Override
  public DonadorDTO buscarDonadorPorID(String donadorID) {
    return donadoresRepository.findById(donadorID)
        .map(dataMapper::toDonadorDTO)
        .orElseThrow(() -> new NoSuchElementException("Donador no encontrado con id: " + donadorID));
  }

  @Override
  public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadDTO) {
    if (entidadDTO == null) throw new RuntimeException();
    //genero id
    String id = (entidadDTO.id() == null || entidadDTO.id().isBlank())
            ? java.util.UUID.randomUUID().toString()
            : entidadDTO.id();
    if (entidadesRepository.findById(id).isPresent()) {
      throw new RuntimeException("ya existe una entidad con el id: " + id);
    }

    EntidadBenefica entidad = dataMapper.toEntidad(entidadDTO);
    entidad.setId(id);
    entidadesRepository.save(entidad);
    return dataMapper.toEntidadDTO(entidad);
  }

  @Override
  public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) {
    return entidadesRepository.findById(entidadID)
        .map(dataMapper::toEntidadDTO)
        .orElseThrow(() -> new NoSuchElementException("Entidad no encontrada"));
  }

  @Override
  @Transactional
  public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadDTO) {
    if (necesidadDTO == null || necesidadDTO.id() != null) throw new RuntimeException();
    if (fachadaDonaciones != null) {
      try {
        fachadaDonaciones.buscarProductoPorID(necesidadDTO.productoSolicitadoID());
      } catch (NoSuchElementException e) {
        throw new IllegalArgumentException("producto solicitado no válido en Donaciones.");
      }
    }

    EntidadBenefica entidadBenefica = entidadesRepository.findById(necesidadDTO.entidadID())
            .orElseThrow(() -> new NoSuchElementException("Entidad no encontrada"));

    int cantidadAAsignar = 0;
    if (fachadaLogistica != null) {
      var stock = fachadaLogistica.consultarStock(necesidadDTO.productoSolicitadoID());
      if (stock != null) {
        int stockDisponible = Math.max(0, stock.cantidadDisponible());
        cantidadAAsignar = Math.min(necesidadDTO.cantidadObjetivo(), stockDisponible);
      }
    }

    NecesidadMaterial necesidadMaterial = dataMapper.toNecesidad(necesidadDTO);
    necesidadMaterial.setId(java.util.UUID.randomUUID().toString());
    necesidadMaterial.setCantidadAsignada(cantidadAAsignar);
    necesidadMaterial.setEntidadBenefica(entidadBenefica);
    entidadBenefica.agregarNecesidad(necesidadMaterial);
    entidadesRepository.saveAndFlush(entidadBenefica);

    if (fachadaLogistica != null && cantidadAAsignar > 0) {
        fachadaLogistica.asignarDesdeStock(
                necesidadMaterial.getId(),
                necesidadDTO.productoSolicitadoID(),
                cantidadAAsignar
        );
    }

    return dataMapper.toNecesidadDTO(necesidadMaterial);


  }

  @Override
  public QuejaDTO agregarQueja(QuejaDTO quejaDTO) {
    if (quejaDTO == null || quejaDTO.id() != null) throw new RuntimeException();
    
    Donador donador = donadoresRepository.findById(quejaDTO.donadorID())
            .orElseThrow(() -> new DonadorNoEncontradoException("Donador no encontrado"));

    Queja queja = new Queja(
            java.util.UUID.randomUUID().toString(),
            quejaDTO.donadorID(),
            quejaDTO.donacionID(),
            quejaDTO.descripcion(),
            quejaDTO.fecha()
    );
    donador.registrarQueja(queja);


    donadoresRepository.save(donador);
    metrics.quejaRegistrada();

    return dataMapper.toQuejaDTO(queja);

  }

  @Override
  public List<QuejaDTO> obtenerQuejasDe(String donadorID) {
    Donador donador = donadoresRepository.findById(donadorID)
        .orElseThrow(() -> new NoSuchElementException("Donador inexistente"));
    return donador.getListaDeQuejas().stream().map(dataMapper::toQuejaDTO).collect(Collectors.toList());
  }

  public List<QuejaDTO> obtenerTodasLasQuejas() {

    List<Donador> todosLosDonadores = donadoresRepository.findAll();
    
    return todosLosDonadores.stream()
            .flatMap(donador -> donador.getListaDeQuejas().stream())
            .map(dataMapper::toQuejaDTO)
            .collect(Collectors.toList());
  }

  @Override
  public Boolean puedeDonar(String donadorID) {
    if (donadorID == null) throw new IllegalArgumentException("ID no puede ser nulo");
    return donadoresRepository.findById(donadorID)
            .map(Donador::puedeHacerDonacion)
            .orElseThrow(() -> new NoSuchElementException("ID no encontrado: " + donadorID));
  }

  @Override
  public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum nuevoEstado) {
    if (donadorID == null || nuevoEstado == null) throw new RuntimeException();
    Donador donador = donadoresRepository.findById(donadorID).orElseThrow(() -> new NoSuchElementException());
    donador.setEstado(nuevoEstado);
    if (nuevoEstado == EstadoDonadorEnum.BANEADO) {
      metrics.donadorBaneado();
    }
    return dataMapper.toDonadorDTO(donadoresRepository.save(donador));
  }

  public DonadorDTO modificarDonador(String donadorID, DonadorDTO donadorDTO) {
    if (donadorID == null || donadorDTO == null) {
      throw new IllegalArgumentException("ID o DTO de donador inválidos");
    }
    Donador donador = donadoresRepository.findById(donadorID)
            .orElseThrow(() -> new NoSuchElementException("Donador no encontrado: " + donadorID));


    if (donadorDTO.nombre() != null) {
      donador.setNombre(donadorDTO.nombre());
    }
    if (donadorDTO.apellido() != null) {
      donador.setApellido(donadorDTO.apellido());
    }
    if (donadorDTO.email() != null) {
      donador.setEmail(donadorDTO.email());
    }
    if (donadorDTO.edad() != null) {
      donador.setEdad(donadorDTO.edad());
    }
    if (donadorDTO.domicilio() != null) {
      donador.setDomicilio(donadorDTO.domicilio());
    }
    if (donadorDTO.nroDocumento() != null) {
      donador.setNroDocumento(donadorDTO.nroDocumento());
    }

    donadoresRepository.save(donador);
    return dataMapper.toDonadorDTO(donador);
  }

  @Override
  public DonadorDTO modifcarCategoria(String donadorID, String nuevaCategoria) {
    if (donadorID == null || nuevaCategoria == null) throw new RuntimeException();
    Donador donador = donadoresRepository.findById(donadorID).orElseThrow(() -> new NoSuchElementException());
    donador.setCategoria(nuevaCategoria);
    donadoresRepository.save(donador);
    return dataMapper.toDonadorDTO(donador);
  }

  @Override
  public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoID) {
    List<NecesidadMaterialDTO> necesidadesInsatisfechas = new ArrayList<>();
    entidadesRepository.findAll().forEach(entidad -> {
      entidad.getNecesidades().stream()
          .filter(necesidad -> productoID.equals(necesidad.getProductoSolicitadoID())
                  && necesidad.getCantidadAsignada() < necesidad.getCantidadObjetivo())
          .map(dataMapper::toNecesidadDTO)
          .forEach(necesidadesInsatisfechas::add);
    });
    return necesidadesInsatisfechas;
  }

@Override
  public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidadASatisfacer) {
    if (necesidadID == null || cantidadASatisfacer == null || cantidadASatisfacer <= 0) throw new RuntimeException();
    
    for (EntidadBenefica entidadBenefica : entidadesRepository.findAll()) {
      for (NecesidadMaterial necesidadMaterial : entidadBenefica.getNecesidades()) {
        if (necesidadID.equals(necesidadMaterial.getId())) {
          
          if (necesidadMaterial.getTipo() == TipoNecesidadMaterialEnum.RECURRENTE) {
              if (!cantidadASatisfacer.equals(necesidadMaterial.getCantidadObjetivo())) {
                  throw new RuntimeException("No se aceptan donaciones parciales para necesidades recurrentes");
              }
          }

          necesidadMaterial.satisfacer(cantidadASatisfacer);
          
          entidadesRepository.save(entidadBenefica);
          
          return dataMapper.toNecesidadDTO(necesidadMaterial);
        }
      }
    }
    throw new NoSuchElementException("Necesidad no encontrada con id: " + necesidadID);
  }

  @Override
  public DonadorStatsDTO estadisticasDonador(String donadorID) {
    Donador donador = donadoresRepository.findById(donadorID).orElseThrow(() -> new NoSuchElementException());
    
    List<String> insigniasIds = (fachadaIncentivos != null) ?
        fachadaIncentivos.getInsigniasDeDonador(donadorID).stream().map(InsigniaDTO::id).collect(Collectors.toList())
        : new ArrayList<>();
    
    MisionDTO misionEnCurso = (fachadaIncentivos != null) ? fachadaIncentivos.getMisionEnCursoDeDonador(donadorID) : null;
    String misionID;
      if (misionEnCurso != null) {
          misionID = misionEnCurso.id();
      } else {
          misionID = null;
      }

    return new DonadorStatsDTO(donador.getId(), donador.getNombre(), donador.getApellido(), donador.getEdad(), 
                               donador.getEstado(), donador.getCategoria(), misionID, insigniasIds);
  }


  @Transactional
  public EntidadBeneficaDTO modificarEntidad(String entidadID, EntidadBeneficaDTO entidadDTO) {
    if (entidadID == null || entidadDTO == null) throw new IllegalArgumentException("Datos inválidos");

    EntidadBenefica entidad = entidadesRepository.findById(entidadID)
            .orElseThrow(() -> new NoSuchElementException("Entidad no encontrada con ID: " + entidadID));

    if (entidadDTO.razonSocial() != null) {
      entidad.setRazonSocial(entidadDTO.razonSocial());
    }
    if (entidadDTO.domicilio() != null) {
      entidad.setDomicilio(entidadDTO.domicilio());
    }
    if (entidadDTO.telefono() != null) {
      entidad.setTelefono(entidadDTO.telefono());
    }
    if (entidadDTO.correo() != null) {
      entidad.setCorreo(entidadDTO.correo());
    }

    entidadesRepository.save(entidad);
    return dataMapper.toEntidadDTO(entidad);
  }

  public NecesidadMaterialDTO buscarNecesidadPorID(String necesidadID) {
    if (necesidadID == null) throw new IllegalArgumentException("ID inválido");

    return necesidadMaterialRepository.findById(necesidadID)
            .map(dataMapper::toNecesidadDTO)
            .orElseThrow(() -> new NoSuchElementException("Necesidad no encontrada con ID: " + necesidadID));
  }

  @Transactional
  public NecesidadMaterialDTO modificarNecesidad(String necesidadID, NecesidadMaterialDTO necesidadDTO) {
    if (necesidadID == null || necesidadDTO == null) throw new IllegalArgumentException("Datos inválidos");

    for (EntidadBenefica entidad : entidadesRepository.findAll()) {
      for (NecesidadMaterial necesidad : entidad.getNecesidades()) {
        if (necesidadID.equals(necesidad.getId())) {

          necesidad.setDescripcion(necesidadDTO.descripcion());
          necesidad.setCantidadObjetivo(necesidadDTO.cantidadObjetivo());

          entidadesRepository.save(entidad);
          return dataMapper.toNecesidadDTO(necesidad);
        }
      }
    }
    throw new NoSuchElementException("Necesidad no encontrada: " + necesidadID);
  }

  @Transactional
  public void borrarNecesidad(String necesidadID) {
    if (necesidadID == null) throw new IllegalArgumentException("ID inválido");

    if (!necesidadMaterialRepository.existsById(necesidadID)) {
      throw new NoSuchElementException("Necesidad no encontrada con ID: " + necesidadID);
    }

    necesidadMaterialRepository.deleteById(necesidadID);
  }


  public List<DonadorDTO> listarDonadores() {
      List<DonadorDTO> dtos = new ArrayList<>();
      
      for (Donador d : donadoresRepository.findAll()) {
          
          dtos.add(dataMapper.toDonadorDTO(d));
      }
      return dtos;
  }

  public List<EntidadBeneficaDTO> listarEntidades() {
      List<EntidadBeneficaDTO> dtos = new ArrayList<>();
      for (EntidadBenefica e : entidadesRepository.findAll()) {
          
          dtos.add(dataMapper.toEntidadDTO(e));
      }
      return dtos;
  }

}