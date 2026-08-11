package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RequestMapping("/necesidades")
@RestController
public class NecesidadController {

    private final Fachada fachada;
    private final DonadorMetricas metrics;

    public NecesidadController(Fachada fachada, DonadorMetricas metrics) {
        this.fachada = fachada;
        this.metrics = metrics;
    }
    @PostMapping
    public ResponseEntity<NecesidadMaterialDTO> registrar(@RequestBody NecesidadMaterialDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fachada.registrarNecesidad(dto));
    }

    @GetMapping
    public ResponseEntity<List<NecesidadMaterialDTO>> listarPorProducto(@RequestParam String productoSolicitadoID) {
        return ResponseEntity.ok(fachada.obtenerNecesidadesInsatisfechasDe(productoSolicitadoID));
    }

    @PostMapping("/{necesidadID}/satisfaccion")
    public ResponseEntity<String> satisfacer(@PathVariable String necesidadID, @RequestParam Integer cantidadASatisfacer) {
        fachada.satisfacerNecesidad(necesidadID, cantidadASatisfacer);
        return ResponseEntity.ok("necesidad Satisfecha!");
    }

    @Operation(summary = "Buscar necesidad por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Necesidad encontrada"),
            @ApiResponse(responseCode = "404", description = "Necesidad no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NecesidadMaterialDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(fachada.buscarNecesidadPorID(id));
    }

    @Operation(summary = "Modificar una necesidad existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Necesidad modificada correctamente"),
            @ApiResponse(responseCode = "404", description = "Necesidad no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NecesidadMaterialDTO> modificar(@PathVariable String id, @RequestBody NecesidadMaterialDTO dto) {
        return ResponseEntity.ok(fachada.modificarNecesidad(id, dto));
    }

    @Operation(summary = "Borrar una necesidad por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Necesidad borrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Necesidad no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable String id) {
        fachada.borrarNecesidad(id);
        return ResponseEntity.ok().build();
    }
}