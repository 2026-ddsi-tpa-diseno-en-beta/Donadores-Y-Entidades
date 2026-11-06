package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Period rollovers are domain changes and must also reach the read model. */
@Component
@ConditionalOnProperty(name = "donatrack.read-model.publisher.enabled", havingValue = "true")
public class ReadModelPeriodJob {
  private final Fachada facade;

  public ReadModelPeriodJob(Fachada facade) {
    this.facade = facade;
  }

  @Scheduled(
      initialDelayString = "${READ_MODEL_SEND_INITIAL_DELAY_MS:1000}",
      fixedDelayString = "${READ_MODEL_PERIOD_INTERVAL_MS:60000}")
  public void refreshPeriods() {
    facade.actualizarPeriodos();
  }
}
