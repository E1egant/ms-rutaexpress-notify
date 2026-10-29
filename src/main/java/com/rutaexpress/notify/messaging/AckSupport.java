package com.rutaexpress.notify.messaging;

import com.rabbitmq.client.Channel;
import com.rutaexpress.contracts.event.MessageEnvelope;
import com.rutaexpress.notify.config.RabbitProperties;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ACK manual y política de errores comunes a los consumidores:
 * envelope inválido o dato inválido (IllegalArgumentException: no se arregla reintentando) -> NACK sin
 * reencolar (DLQ) de inmediato; error transitorio -> reintentos en proceso y,
 * al agotarlos, NACK sin reencolar (DLQ). Todo envío a DLQ queda en el log.
 */
@Component
public class AckSupport {

    private static final Logger log = LoggerFactory.getLogger(AckSupport.class);

    private final int maxAttempts;

    public AckSupport(RabbitProperties properties) {
        this.maxAttempts = Math.max(1, properties.retry().maxAttempts());
    }

    public void handle(String queue, MessageEnvelope<?> envelope, Channel channel, long tag, Runnable work)
            throws IOException {
        if (envelope == null || envelope.eventId() == null || envelope.payload() == null) {
            log.warn("Mensaje inválido en {} (sin eventId/payload), enviado a la DLQ", queue);
            channel.basicNack(tag, false, false);
            return;
        }
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                work.run();
                channel.basicAck(tag, false);
                return;
            } catch (IllegalArgumentException e) {
                log.error("Mensaje {} con datos inválidos en {}, enviado a la DLQ sin reintentos: {}",
                        envelope.eventId(), queue, e.getMessage());
                channel.basicNack(tag, false, false);
                return;
            } catch (RuntimeException e) {
                last = e;
                log.warn("Fallo procesando {} en {} (intento {}/{})", envelope.eventId(), queue, attempt,
                        maxAttempts, e);
            }
        }
        log.error("Mensaje {} agotó los reintentos en {}, enviado a la DLQ: {}", envelope.eventId(), queue,
                last.getMessage());
        channel.basicNack(tag, false, false);
    }
}
