package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.Fachada;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/entidades") 
@RestController
public class EntidadController {

    private final Fachada fachada;
    private final DonadorMetricas metrics;

    public EntidadController(Fachada fachada, DonadorMetricas metrics) {
        this.fachada = fachada;
        this.metrics = metrics;
    }

    @PostMapping
    public ResponseEntity<EntidadBeneficaDTO> registrar(@RequestBody EntidadBeneficaDTO dto) {
        EntidadBeneficaDTO nueva = fachada.agregarEntidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping
    public ResponseEntity<List<EntidadBeneficaDTO>> listar() {
        return ResponseEntity.ok(fachada.listarEntidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadBeneficaDTO> buscar(@PathVariable String id) {
        return ResponseEntity.ok(fachada.buscarEntidadPorID(id));
    }

    @Operation(summary = "Modificar los datos de una Entidad Benéfica existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entidad modificada correctamente"),
            @ApiResponse(responseCode = "404", description = "Entidad no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntidadBeneficaDTO> modificarEntidad(@PathVariable String id, @RequestBody EntidadBeneficaDTO dto) {
        return ResponseEntity.ok(fachada.modificarEntidad(id, dto));
    }
}