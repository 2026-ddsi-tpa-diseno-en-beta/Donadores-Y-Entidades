package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/sistema")
public class SistemaController {

    @Autowired
    private Fachada fachada;

    @Autowired
    private DonadoresRepository donadorRepository;
    @Autowired
    private EntidadesRepository entidadesRepository;
    @Autowired
    private QuejaRepository quejaRepository;
    @Autowired
    private NecesidadMaterialRepository necesidadRepository;

    @DeleteMapping("/base-de-datos")
    public ResponseEntity<String> limpiarBaseDeDatos() {
        
        quejaRepository.deleteAll();
        necesidadRepository.deleteAll();
        donadorRepository.deleteAll();
        entidadesRepository.deleteAll();
        
        return ResponseEntity.ok("Base de datos limpia");
    }

    @GetMapping("/donadores")
    public ResponseEntity<?> obtenerTodosLosDonadores() {
        return ResponseEntity.ok(fachada.listarDonadores());
    }

    @GetMapping("/entidades")
    public ResponseEntity<?> obtenerTodasLasEntidades() {
        return ResponseEntity.ok(fachada.listarEntidades());
    }
}