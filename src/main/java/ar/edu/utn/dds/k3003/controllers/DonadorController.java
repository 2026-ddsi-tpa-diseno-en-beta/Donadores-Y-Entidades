package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/donadores")
public class DonadorController {

    @Autowired
    private Fachada fachada;

    @PostMapping // POST /donadores (No modificable)
    public ResponseEntity<DonadorDTO> registrar(@RequestBody DonadorDTO dto) {
        return ResponseEntity.ok(fachada.agregarDonador(dto));
    }

    @GetMapping // GET /donadores (No modificable - Lista completa)
    public ResponseEntity<List<DonadorDTO>> listar() {
        // Acá devolvés todos los donadores que tengas en memoria
        return ResponseEntity.ok(fachada.listarDonadores()); 
    }

    @GetMapping("/{id}") // GET /donadores/{id} (No modificable)
    public ResponseEntity<DonadorDTO> buscar(@PathVariable String id) {
        return ResponseEntity.ok(fachada.buscarDonadorPorID(id));
    }


    @PostMapping("/{id}/quejas") // POST /donadores/{id}/quejas
    public ResponseEntity<QuejaDTO> registrarQueja(@PathVariable String id, @RequestBody QuejaDTO dto) {
     QuejaDTO nuevaQueja = new QuejaDTO(
            null,              
            null,               
            id,                 
            dto.fecha(),        
            dto.descripcion()   
        );
        
        return ResponseEntity.ok(fachada.agregarQueja(nuevaQueja));
    }
}