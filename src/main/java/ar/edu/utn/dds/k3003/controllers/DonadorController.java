package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donadores")
public class DonadorController {

    private final Fachada fachada;
    private final DonadorMetricas metrics;

    public DonadorController(Fachada fachada, DonadorMetricas metrics) {
        this.fachada = fachada;
        this.metrics = metrics;
    }

    @PostMapping // POST /donadores (No modificable)
    public ResponseEntity<DonadorDTO> registrar(@RequestBody DonadorDTO dto) {
        DonadorDTO nuevoDonador = fachada.agregarDonador(dto);
        metrics.donadorRegistrado();
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDonador);
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
            dto.donacionID(),
            id,
            dto.fecha(),
            dto.descripcion()
        );
        QuejaDTO resultado = fachada.agregarQueja(nuevaQueja);
        metrics.quejaRegistrada();
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
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

    @GetMapping("/quejas/todas")
    public ResponseEntity<List<QuejaDTO>> listarTodasLasQuejas() {
        return ResponseEntity.ok(fachada.obtenerTodasLasQuejas());
    }
}
