package com.rutaexpress.contracts.dto;

import java.time.Instant;

/**
 * Notificación enviada/registrada por el servicio de notificaciones.
 */
public record NotificationDto(
        Long id,
        String channel,
        String recipient,
        String subject,
        String body,
        Instant createdAt) {
}
