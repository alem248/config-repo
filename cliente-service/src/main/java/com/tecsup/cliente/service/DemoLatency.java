package com.tecsup.cliente.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DemoLatency {

    private static final Logger log = LoggerFactory.getLogger(DemoLatency.class);
    private final AtomicBoolean enabled;
    private final long delayMs;

    public DemoLatency(
            @Value("${demo.latency.enabled:false}") boolean initiallyEnabled,
            @Value("${demo.latency.delay-ms:5000}") long delayMs) {
        this.enabled = new AtomicBoolean(initiallyEnabled);
        this.delayMs = delayMs;
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
        log.info("Latencia de demostración {}", value ? "activada" : "desactivada");
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void pause(String serviceName) {
        if (!enabled.get()) {
            return;
        }
        log.info("DEMO_LATENCY service={} delayMs={}", serviceName, delayMs);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La espera de demostración fue interrumpida", ex);
        }
    }
}
