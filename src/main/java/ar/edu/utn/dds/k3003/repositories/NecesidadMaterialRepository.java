package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.NecesidadMaterial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NecesidadMaterialRepository extends JpaRepository<NecesidadMaterial, String> {
  List<NecesidadMaterial> findByProductoSolicitadoID(String productoSolicitadoID);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query(
      "select n from NecesidadMaterial n where n.id = :id")
  java.util.Optional<NecesidadMaterial> findByIdForUpdate(
      @org.springframework.data.repository.query.Param("id") String id);
}
