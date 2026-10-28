package com.rutaexpress.contracts.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Envelope común de los mensajes de RabbitMQ. eventId es la clave de idempotencia.
 */
public record MessageEnvelope<T>(
        String type,
        UUID eventId,
        Instant timestamp,
        String traceId,
        String correlationId,
        T payload) {
}
