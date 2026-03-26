package com.datvexemphim;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    // No-op: null = normal (enabled=true), false = locked, true = active
    // New users are registered with enabled=true by default

    @Override
    public void run(String... args) {
        // intentionally empty
    }
}
