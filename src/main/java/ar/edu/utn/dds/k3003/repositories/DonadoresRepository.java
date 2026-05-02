package ar.edu.utn.dds.k3003.repositories;

import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Donador;
import java.util.*;

@Repository
public interface DonadoresRepository {
    Donador save(Donador donador);
    Optional<Donador> findById(String id);
    void clear();
    List<Donador> findAll();
}

