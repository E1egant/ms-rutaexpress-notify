package com.rutaexpress.notify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nombres de exchanges, colas y routing keys, tomados de rutaexpress.rabbit.* en application.yml.
 */
@ConfigurationProperties(prefix = "rutaexpress.rabbit")
public record RabbitProperties(Exchanges exchanges, Queues queues, RoutingKeys routing,
        Retry retry) {

    public record Exchanges(String direct, String topic, String dead) {
    }

    public record Queues(String email, String warehouse, String label, String dlqSuffix) {
    }

    public record RoutingKeys(String emailSend, String warehouseTicket, String labelGen,
            String emailPattern, String warehousePattern, String labelPattern,
            String emailDead, String warehouseDead, String labelDead) {
    }

    public record Retry(int maxAttempts) {
    }
}
