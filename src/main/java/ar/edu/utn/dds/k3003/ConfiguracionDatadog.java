package ar.edu.utn.dds.k3003;

import io.micrometer.datadog.DatadogConfig; // Clase de la librería
import io.micrometer.datadog.DatadogMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ConfiguracionDatadog { 

    @Bean
    public MeterRegistry meterRegistry() {
        String apiKey = System.getenv("DD_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            return new SimpleMeterRegistry();
        }


        DatadogConfig config = new DatadogConfig() {
            @Override public String get(String k) { return null; }
            @Override public String apiKey() { return apiKey; }
            @Override public String uri() { return "https://api.us5.datadoghq.com"; }
            @Override public Duration step() { return Duration.ofMinutes(1); }
        };

        return new DatadogMeterRegistry(config, io.micrometer.core.instrument.Clock.SYSTEM);
    }
}
