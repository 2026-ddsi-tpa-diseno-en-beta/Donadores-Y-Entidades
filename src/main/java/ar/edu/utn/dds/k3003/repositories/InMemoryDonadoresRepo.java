package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDonadoresRepo implements DonadoresRepository {

  private static Map<String, Donador> donadores = new HashMap<>();

  @Override
  public Donador save(Donador donador) {
    donadores.put(donador.getId(), donador);
    return donador;
  }

  @Override
  public Optional<Donador> findById(String id) {
    return Optional.ofNullable(donadores.get(id));
  }

  @Override
  public void clear() {
   donadores.clear();
  }
  public List<Donador> findAll() {
    return new ArrayList<>(donadores.values());
  }
}