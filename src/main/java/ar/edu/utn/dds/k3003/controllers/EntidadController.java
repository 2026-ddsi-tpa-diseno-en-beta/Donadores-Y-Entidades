package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.Fachada;

import java.util.List;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NoArgsConstructor
@RequestMapping("/entidades") 
@RestController
public class EntidadController {

    @Autowired
    private Fachada fachada;

    @PostMapping
    public ResponseEntity<EntidadBeneficaDTO> registrar(@RequestBody EntidadBeneficaDTO dto) {
        return ResponseEntity.ok(fachada.agregarEntidad(dto));
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