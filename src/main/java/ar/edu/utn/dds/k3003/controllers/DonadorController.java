package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donadores")
public class DonadorController {

    @Autowired
    private Fachada fachada;

    @PostMapping // POST /donadores (No modificable)
    public ResponseEntity<DonadorDTO> registrar(@RequestBody DonadorDTO dto) {
        return ResponseEntity.ok(fachada.agregarDonador(dto));
    }

    @GetMapping // GET /donadores (No modificable)
    public ResponseEntity<List<DonadorDTO>> listar() {
        
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

    @GetMapping("/{id}/quejas")
    public ResponseEntity<List<QuejaDTO>> listarQuejas(@PathVariable String id) {
        return ResponseEntity.ok(fachada.obtenerQuejasDe(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DonadorDTO> actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        String estadoString = body.get("estado");
        EstadoDonadorEnum nuevoEstado = EstadoDonadorEnum.valueOf(estadoString);
        return ResponseEntity.ok(fachada.modificarEstado(id, nuevoEstado));
    }

    @PatchMapping("/{id}/categoria")
    public ResponseEntity<DonadorDTO> actualizarCategoria(@PathVariable String id, @RequestBody Map<String, String> body) {
        String nuevaCat = body.get("categoria");
        return ResponseEntity.ok(fachada.modifcarCategoria(id, nuevaCat));
    }

    @GetMapping("/{id}/puede-donar")
    public ResponseEntity<Boolean> puedeDonar(@PathVariable String id) {
        return ResponseEntity.ok(fachada.puedeDonar(id));
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<Object> obtenerStats(@PathVariable String id) {
        return ResponseEntity.ok(fachada.estadisticasDonador(id));
    }
}