package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donador;
import java.util.*;

public interface DonadoresRepository {
    Donador save(Donador donador);
    Optional<Donador> findById(String id);
    void clear();
    List<Donador> findAll();
}

