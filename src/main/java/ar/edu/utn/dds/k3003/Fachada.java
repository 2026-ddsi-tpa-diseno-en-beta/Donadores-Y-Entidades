package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.*;
import java.util.*;



import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;


@SpringBootApplication(scanBasePackages = "ar.edu.utn.dds.k3003") 
@EnableJpaRepositories(basePackages = "ar.edu.utn.dds.k3003.repositories") 
@EntityScan(basePackages = "ar.edu.utn.dds.k3003.model")

@Service
public class Fachada implements FachadaDonadoresYEntidades {

  
  @Autowired
  private DonadoresRepository donadoresRepository;
  private EntidadesRepository entidadesRepository;
  
  private DonadoresYEntidadesDataMapper dataMapper = new DonadoresYEntidadesDataMapper();
  private FachadaIncentivos fachadaIncentivos;
  private int idCounter = 1;

  @Autowired (required = false)
  public Fachada(DonadoresRepository donadoresRepository, EntidadesRepository entidadesRepository) {
      this.donadoresRepository = donadoresRepository;
      this.entidadesRepository = entidadesRepository;
  }

  public Fachada() {
  }

  @Override
  public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {
    this.fachadaIncentivos = fachadaIncentivos;
  }

  @Override
  public DonadorDTO agregarDonador(@Valid @RequestBody DonadorDTO donadorDTO) {
    if (donadorDTO == null) throw new RuntimeException();
    
    if (donadorDTO.id() != null && donadoresRepository.findById(donadorDTO.id()).isPresent()) {
        throw new RuntimeException();
    }
    
    Donador donadorModel = dataMapper.toDonador(donadorDTO);
    if (donadorModel.getId() == null) {
        donadorModel.setId(String.valueOf(idCounter++));
    }
    
    donadoresRepository.save(donadorModel);
    return dataMapper.toDonadorDTO(donadorModel);
  }

  @Override
  public DonadorDTO buscarDonadorPorID(String donadorID) {
    return donadoresRepository.findById(donadorID)
        .map(dataMapper::toDonadorDTO)
        .orElseThrow(() -> new NoSuchElementException("Donador no encontrado: " + donadorID));
  }

  @Override
  public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadDTO) {
    if (entidadDTO == null) throw new RuntimeException();
    
    if (entidadDTO.id() != null && entidadesRepository.findById(entidadDTO.id()).isPresent()) {
        throw new RuntimeException();
    }
    
    EntidadBenefica entidadBenefica = dataMapper.toEntidad(entidadDTO);
    if (entidadBenefica.getId() == null) {
        entidadBenefica.setId(String.valueOf(idCounter++));
    }
    entidadesRepository.save(entidadBenefica);
    return dataMapper.toEntidadDTO(entidadBenefica);
  }

  @Override
  public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) {
    return entidadesRepository.findById(entidadID)
        .map(dataMapper::toEntidadDTO)
        .orElseThrow(() -> new NoSuchElementException("Entidad no encontrada"));
  }

  @Override
  public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadDTO) {
    if (necesidadDTO == null || necesidadDTO.id() != null) throw new RuntimeException();
    
    EntidadBenefica entidadBenefica = entidadesRepository.findById(necesidadDTO.entidadID()).orElseGet(() -> {
        EntidadBenefica nuevaEntidad = new EntidadBenefica();
        nuevaEntidad.setId(necesidadDTO.entidadID());
        return entidadesRepository.save(nuevaEntidad);
    });

    NecesidadMaterial necesidadMaterial = dataMapper.toNecesidad(necesidadDTO);
    necesidadMaterial.setId("N-" + idCounter++);
    entidadBenefica.agregarNecesidad(necesidadMaterial);
    entidadesRepository.save(entidadBenefica);
    return dataMapper.toNecesidadDTO(necesidadMaterial);
  }

  @Override
  public QuejaDTO agregarQueja(QuejaDTO quejaDTO) {
    if (quejaDTO == null || quejaDTO.id() != null) throw new RuntimeException();
    
    Donador donador = donadoresRepository.findById(quejaDTO.donadorID())
            .orElseThrow(() -> new DonadorNoEncontradoException("Donador no encontrado"));

    Queja queja = new Queja(String.valueOf(idCounter++), quejaDTO.donadorID(), quejaDTO.donacionID(), quejaDTO.descripcion(), quejaDTO.fecha());
    donador.registrarQueja(queja);
    donadoresRepository.save(donador);
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