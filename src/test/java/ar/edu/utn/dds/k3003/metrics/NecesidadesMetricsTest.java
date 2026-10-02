package ar.edu.utn.dds.k3003.metrics;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NecesidadesMetricsTest {
  @Test void expiredPeriodDoesNotMutatePersistedDeliveryAndExtraordinaryRemainsPartial() {
    var recurrent = new NecesidadRecurrente();
    recurrent.setTipo(TipoNecesidadMaterialEnum.RECURRENTE); recurrent.setCantidadObjetivo(10);
    recurrent.setCantidadAsignada(10); recurrent.setInicioPeriodo(LocalDate.of(2026, 9, 21));
    var extra = new NecesidadMaterial("e", "arroz", 10, 1, "entidad", "producto", TipoNecesidadMaterialEnum.EXTRAORDINARIA);
    extra.setCantidadAsignada(4);
    var data = NecesidadesMetrics.summarize(List.of(recurrent, extra), LocalDate.of(2026, 10, 2));
    assertArrayEquals(new double[]{1,10,0,10}, data.get("RECURRENTE"));
    assertArrayEquals(new double[]{1,10,4,6}, data.get("EXTRAORDINARIA"));
    assertEquals(10, recurrent.getCantidadAsignada());
    assertEquals(LocalDate.of(2026,9,21), recurrent.getInicioPeriodo());
  }
}
