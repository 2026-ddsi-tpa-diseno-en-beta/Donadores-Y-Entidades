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
    private final Counter necesidadesRegistradas;
    private final Counter unidadesEntregadas;

    public DonadorMetricas(MeterRegistry registry) {
        necesidadesRegistradas = Counter.builder("donadores.necesidades.registradas")
                .description("Necesidades creadas luego de completar la reserva de stock")
                .register(registry);
        unidadesEntregadas = Counter.builder("donadores.necesidades.unidades.entregadas")
                .description("Unidades recibidas físicamente por las entidades")
                .register(registry);
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
    public void necesidadRegistrada() { necesidadesRegistradas.increment(); }
    public void unidadesEntregadas(int cantidad) { unidadesEntregadas.increment(cantidad); }
}
