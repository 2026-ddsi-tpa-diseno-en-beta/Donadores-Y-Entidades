package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonadoresRepository extends JpaRepository<Donador, String> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select d from Donador d where d.id = :id")
  java.util.Optional<Donador> findByIdForUpdate(
      @org.springframework.data.repository.query.Param("id") String id);
}
