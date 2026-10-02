package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PeriodoNecesidadTest {
  NecesidadMaterial need(PeriodoNecesidad periodo) {
    var n=new NecesidadMaterial("n","Recurrente",10,5,"e","p",TipoNecesidadMaterialEnum.RECURRENTE);
    n.setPeriodo(periodo);return n;
  }
  @Test void semanalRenuevaSoloAlCambiarLaSemana() {
    var n=need(PeriodoNecesidad.SEMANAL); n.actualizarPeriodo(LocalDate.of(2026,9,28)); n.setCantidadAsignada(10);
    n.actualizarPeriodo(LocalDate.of(2026,10,4));assertEquals(10,n.getCantidadAsignada());
    n.actualizarPeriodo(LocalDate.of(2026,10,5));assertEquals(0,n.getCantidadAsignada());
  }
  @Test void mensualNoSeReiniciaAlCambiarLaSemana() {
    var n=need(PeriodoNecesidad.MENSUAL);n.actualizarPeriodo(LocalDate.of(2026,10,2));n.setCantidadAsignada(10);
    n.actualizarPeriodo(LocalDate.of(2026,10,26));assertEquals(10,n.getCantidadAsignada());
    n.actualizarPeriodo(LocalDate.of(2026,11,1));assertEquals(0,n.getCantidadAsignada());
  }
  @Test void extraordinariaNoPierdeLoRecibidoConElTiempo() {
    var n=need(PeriodoNecesidad.SEMANAL);n.setTipo(TipoNecesidadMaterialEnum.EXTRAORDINARIA);n.setCantidadAsignada(3);
    n.actualizarPeriodo(LocalDate.of(2027,1,1));assertEquals(3,n.getCantidadAsignada());assertNull(n.getInicioPeriodo());
  }
}
