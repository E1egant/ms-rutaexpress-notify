package com.rutaexpress.notify.messaging.email;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.Channel;
import com.rutaexpress.contracts.event.EmailPayload;
import com.rutaexpress.contracts.event.MessageEnvelope;
import com.rutaexpress.notify.config.RabbitProperties;
import com.rutaexpress.notify.messaging.AckSupport;
import com.rutaexpress.notify.service.NotificationService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailConsumerTest {

    @Test
    void elMensajeSeProcesaYSeConfirma() throws Exception {
        NotificationService service = mock(NotificationService.class);
        Channel channel = mock(Channel.class);
        EmailConsumer consumer = new EmailConsumer(service,
                new AckSupport(new RabbitProperties(null, null, null, new RabbitProperties.Retry(3))));
        UUID id = UUID.randomUUID();

        consumer.onEmail(new MessageEnvelope<>("email.send", id, Instant.now(), "t", "c",
                new EmailPayload("email", "a@b.cl", "Asunto", "Cuerpo")), channel, 1L);

        verify(service).process(id, "email", "a@b.cl", "Asunto", "Cuerpo");
        verify(channel).basicAck(1L, false);
    }
}
