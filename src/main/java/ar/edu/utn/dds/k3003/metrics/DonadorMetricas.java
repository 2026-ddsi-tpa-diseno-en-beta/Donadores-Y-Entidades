package ar.edu.utn.dds.k3003.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class DonadorMetricas {
    private final Counter donadoresRegistrados;
    private final Counter quejasRegistradas;
    private final Counter donadoresBaneados;
    private final Counter errores;

    public DonadorMetricas(MeterRegistry registry) {
        donadoresRegistrados = Counter.builder("donadores.registrados")
                .description("Cantidad de donadores creados")
                .register(registry);
        quejasRegistradas = Counter.builder("donadores.quejas.registradas")
                .description("Cantidad de quejas recibidas")
                .register(registry);
        donadoresBaneados = Counter.builder("donadores.baneados")
                .description("Cantidad de donadores baneados")
                .register(registry);
        errores = Counter.builder("donadores.errores")
                .description("Cantidad de errores en la fachada")
                .register(registry);
    }

    public void donadorRegistrado() { donadoresRegistrados.increment(); }
    public void quejaRegistrada() { quejasRegistradas.increment(); }
    public void donadorBaneado() { donadoresBaneados.increment(); }
    public void error() { errores.increment(); }
}