package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.Fachada; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entidades") 
public class EntidadController {

    @Autowired
    private Fachada fachada;

    // 1. REGISTRAR ENTIDAD (POST /entidades)
    @PostMapping
    public EntidadBeneficaDTO agregar(@RequestBody EntidadBeneficaDTO entidadDTO) {
        return fachada.agregarEntidad(entidadDTO);
    }

    // 2. BUSCAR ENTIDAD (GET /entidades/{id})
    @GetMapping("/{id}")
    public EntidadBeneficaDTO buscar(@PathVariable String id) {
        return fachada.buscarEntidadPorID(id); 
    }
}