package com.tecsup.producto.controller;

import com.tecsup.producto.service.DemoLatency;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/api/demo/latencia")
public class DemoLatencyController {

    private final DemoLatency demoLatency;

    public DemoLatencyController(DemoLatency demoLatency) {
        this.demoLatency = demoLatency;
    }

    @GetMapping
    public Map<String, Object> estado() {
        return Map.of("enabled", demoLatency.isEnabled(), "delayMs", demoLatency.getDelayMs());
    }

    @PutMapping
    public Map<String, Object> configurar(@RequestParam boolean enabled) {
        demoLatency.setEnabled(enabled);
        return estado();
    }
}
