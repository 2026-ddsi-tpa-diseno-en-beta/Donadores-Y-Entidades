package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.utn.dds.k3003.model.Queja;

@Repository
public interface QuejaRepository extends JpaRepository<Queja, String> {
}