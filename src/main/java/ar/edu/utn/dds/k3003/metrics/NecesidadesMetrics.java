package ar.edu.utn.dds.k3003.metrics;

import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.NecesidadMaterialRepository;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;

/** Snapshot of persisted physical deliveries, never performs reservations or resets periods. */
@Component
public class NecesidadesMetrics {
  private final NecesidadMaterialRepository repository;
  private long refreshed;
  private Map<String, double[]> snapshot = Map.of();

  public NecesidadesMetrics(MeterRegistry registry, NecesidadMaterialRepository repository) {
    this.repository = repository;
    for (String type : List.of("EXTRAORDINARIA", "RECURRENTE")) {
      String[] names = {"activas", "unidades.objetivo", "unidades.cubiertas", "unidades.faltantes"};
      for (int i = 0; i < names.length; i++) {
        int index = i;
        Gauge.builder("donadores.necesidades." + names[i], () -> value(type, index))
            .tag("tipo", type).description("Estado del período actual; cubiertas por entregas físicas hasta el objetivo; no reservas")
            .register(registry);
      }
    }
  }

  private synchronized double value(String type, int index) {
    if (System.currentTimeMillis() - refreshed > 10_000) {
      snapshot = summarize(repository.findAll(), LocalDate.now());
      refreshed = System.currentTimeMillis();
    }
    return snapshot.getOrDefault(type, new double[4])[index];
  }

  static Map<String, double[]> summarize(List<NecesidadMaterial> needs, LocalDate today) {
    Map<String, double[]> totals = new HashMap<>();
    for (NecesidadMaterial need : needs) {
      if (need.getTipo() == null || need.getCantidadObjetivo() == null) continue;
      boolean recurrent = need.getTipo().name().equals("RECURRENTE");
      int received = need.getCantidadAsignada() == null ? 0 : need.getCantidadAsignada();
      if (recurrent && need.getInicioPeriodo() != null && !need.getInicioPeriodo().equals(
          (need.getPeriodo() == null ? PeriodoNecesidad.SEMANAL : need.getPeriodo()).inicio(today))) received = 0;
      int target = Math.max(0, need.getCantidadObjetivo());
      int missing = Math.max(0, target - received);
      double[] values = totals.computeIfAbsent(need.getTipo().name(), key -> new double[4]);
      if (missing > 0) values[0]++;
      values[1] += target; values[2] += received; values[3] += missing;
    }
    return totals;
  }
}
