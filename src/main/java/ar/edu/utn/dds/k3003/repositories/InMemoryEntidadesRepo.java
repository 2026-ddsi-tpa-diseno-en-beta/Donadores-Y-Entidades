package ar.edu.utn.dds.k3003.repositories;
import ar.edu.utn.dds.k3003.model.EntidadBenefica;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryEntidadesRepo implements EntidadesRepository {

  private Map<String, EntidadBenefica> mapaDeEntidades = new HashMap<>();

  @Override
  public EntidadBenefica save(EntidadBenefica entidadParaGuardar) {
    this.mapaDeEntidades.put(entidadParaGuardar.getEntidadID(), entidadParaGuardar);
    return entidadParaGuardar;
  }

  @Override
  public Optional<EntidadBenefica> findById(String entidadID) {
    return Optional.ofNullable(this.mapaDeEntidades.get(entidadID));
  }

  @Override
  public List<EntidadBenefica> findAll() {
    return new ArrayList<>(this.mapaDeEntidades.values());
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'clear'");
  }

  public List<EntidadBenefica> all() {
    return findAll();
  }
  
}
