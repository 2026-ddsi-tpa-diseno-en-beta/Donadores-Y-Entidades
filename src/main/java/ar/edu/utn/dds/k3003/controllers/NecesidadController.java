package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@NoArgsConstructor
@RequestMapping("/necesidades")
@RestController
public class NecesidadController {

    @Autowired
    private Fachada fachada;

    @PostMapping
    public ResponseEntity<NecesidadMaterialDTO> registrar(@RequestBody NecesidadMaterialDTO dto) {
        return ResponseEntity.ok(fachada.registrarNecesidad(dto));
    }

    @GetMapping
    public ResponseEntity<List<NecesidadMaterialDTO>> listarPorProducto(@RequestParam String productoSolicitadoID) {
        return ResponseEntity.ok(fachada.obtenerNecesidadesInsatisfechasDe(productoSolicitadoID));
    }

    @PostMapping("/{necesidadID}/satisfaccion")
    public ResponseEntity<String> satisfacer(@PathVariable String necesidadID, @RequestParam Integer cantidadASatisfacer) {
        try {
            fachada.satisfacerNecesidad(necesidadID, cantidadASatisfacer);
            return ResponseEntity.ok("Satisfecha!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}