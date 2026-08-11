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
  private FachadaIncentivos fachadaIncentivos;
  private FachadaDonaciones fachadaDonaciones;
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

    if (donadorDTO.id() != null && donadoresRepository.findById(donadorDTO.id()).isPresent()) {
        throw new NoSuchElementException("Error: Ya existe un donador con el ID: " + donadorDTO.id());
    }

    Donador donadorModel = dataMapper.toDonador(donadorDTO);
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
    
    if (entidadDTO.id() != null && entidadesRepository.findById(entidadDTO.id()).isPresent()) {
        throw new RuntimeException();
    }

    EntidadBenefica entidad = dataMapper.toEntidad(entidadDTO);
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
      int stockDisponible = stock.cantidadDisponible();

      cantidadAAsignar = Math.min(necesidadDTO.cantidadObjetivo(), stockDisponible);
    }

    NecesidadMaterial necesidadMaterial = dataMapper.toNecesidad(necesidadDTO);
    necesidadMaterial.setId(java.util.UUID.randomUUID().toString());
    necesidadMaterial.setCantidadAsignada(cantidadAAsignar);

    entidadBenefica.agregarNecesidad(necesidadMaterial);
    entidadesRepository.saveAndFlush(entidadBenefica);

    if (fachadaLogistica != null && cantidadAAsignar > 0) {
        fachadaLogistica.asignarDesdeStock(
                necesidadMaterial.getId(),
                necesidadDTO.productoSolicitadoID(),
                necesidadDTO.cantidadObjetivo()
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
            String.valueOf(idCounter++),
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
    if (donadorID == null) {
        return donadoresRepository.findAll().stream()
            .findFirst()
            .map(Donador::puedeHacerDonacion)
            .orElseThrow(() -> new NoSuchElementException("No hay donadores en el repo"));
    }

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
          .filter(necesidad -> productoID.equals(necesidad.getProductoSolicitadoID()) && necesidad.getCantidadObjetivo() > 0)
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
              if (cantidadASatisfacer < necesidadMaterial.getCantidadObjetivo()) {
                  throw new RuntimeException("No se aceptan donaciones parciales para necesidades recurrentes");
              }
          }

          necesidadMaterial.setCantidadObjetivo(Math.max(0, necesidadMaterial.getCantidadObjetivo() - cantidadASatisfacer));
          
          entidadesRepository.save(entidadBenefica);
          
          return dataMapper.toNecesidadDTO(necesidadMaterial);
        }
      }
    }
    throw new NoSuchElementException();
  }

  @Override
  public DonadorStatsDTO estadisticasDonador(String donadorID) {
    Donador donador = donadoresRepository.findById(donadorID).orElseThrow(() -> new NoSuchElementException());
    
    List<String> insigniasNombres = (fachadaIncentivos != null) ? 
        fachadaIncentivos.getInsigniasDeDonador(donadorID).stream().map(InsigniaDTO::nombre).collect(Collectors.toList()) 
        : new ArrayList<>();
    
    MisionDTO misionEnCurso = (fachadaIncentivos != null) ? fachadaIncentivos.getMisionEnCursoDeDonador(donadorID) : null;
    String misionID;
      if (misionEnCurso != null) {
          misionID = misionEnCurso.id();
      } else {
          misionID = null;
      }

    return new DonadorStatsDTO(donador.getId(), donador.getNombre(), donador.getApellido(), donador.getEdad(), 
                               donador.getEstado(), donador.getCategoria(), misionID, insigniasNombres);
  }


  @Transactional
  public EntidadBeneficaDTO modificarEntidad(String entidadID, EntidadBeneficaDTO entidadDTO) {
    if (entidadID == null || entidadDTO == null) throw new IllegalArgumentException("Datos inválidos");

    EntidadBenefica entidad = entidadesRepository.findById(entidadID)
            .orElseThrow(() -> new NoSuchElementException("Entidad no encontrada con ID: " + entidadID));

    // Actualizamos los campos
    entidad.setRazonSocial(entidadDTO.razonSocial());
    entidad.setCorreo(entidadDTO.correo());

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

    NecesidadMaterial necesidad = necesidadMaterialRepository.findById(necesidadID)
            .orElseThrow(() -> new NoSuchElementException("Necesidad no encontrada con ID: " + necesidadID));

    necesidad.setDescripcion(necesidadDTO.descripcion());
    necesidad.setCantidadObjetivo(necesidadDTO.cantidadObjetivo());

    necesidadMaterialRepository.save(necesidad);
    return dataMapper.toNecesidadDTO(necesidad);
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