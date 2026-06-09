package ar.edu.utn.dds.k3003;

import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.Duration;

@Configuration
public class ConfiguracionDatadog {

    @Bean
    @ConditionalOnProperty(name = "DD_ENABLED", havingValue = "true")
    public MeterRegistry datadogMeterRegistry() {
        DatadogConfig config = new DatadogConfig() {
            @Override public String get(String k) { return null; }
            @Override public String apiKey() { return System.getenv("DD_API_KEY"); }
            @Override public String uri() { return "https://api.us5.datadoghq.com"; }
            @Override public Duration step() { return Duration.ofMinutes(1); }
        };
        return new DatadogMeterRegistry(config, io.micrometer.core.instrument.Clock.SYSTEM);
    }

    @Bean
    @ConditionalOnProperty(name = "DD_ENABLED", havingValue = "false", matchIfMissing = true)
    public MeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}
