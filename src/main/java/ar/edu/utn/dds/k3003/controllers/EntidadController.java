package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.Fachada;

import java.util.List;

import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
}