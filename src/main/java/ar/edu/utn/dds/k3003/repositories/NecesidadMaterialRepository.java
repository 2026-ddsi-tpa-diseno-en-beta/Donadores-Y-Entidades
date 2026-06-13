package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.utn.dds.k3003.model.NecesidadMaterial;

import java.util.List;

@Repository
public interface NecesidadMaterialRepository extends JpaRepository<NecesidadMaterial, String> {
    List<NecesidadMaterial> findByProductoSolicitadoID(String productoSolicitadoID);
}