package ar.edu.utn.dds.k3003.repositories;

import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Donador;
import ar.edu.utn.dds.k3003.model.EntidadBenefica;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntidadesRepository {
    EntidadBenefica save(EntidadBenefica entidad);
    Optional<EntidadBenefica> findById(String id);
    List<EntidadBenefica> all();
    List<EntidadBenefica> findAll();
    void clear();
}