package com.rutaexpress.notify.messaging;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.Channel;
import com.rutaexpress.contracts.event.EmailPayload;
import com.rutaexpress.contracts.event.MessageEnvelope;
import com.rutaexpress.notify.config.RabbitProperties;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AckSupportTest {

    private final Channel channel = mock(Channel.class);
    private final AckSupport ack = new AckSupport(
            new RabbitProperties(null, null, null, new RabbitProperties.Retry(3)));

    private MessageEnvelope<EmailPayload> envelope(UUID id, EmailPayload payload) {
        return new MessageEnvelope<>("email.send", id, Instant.now(), "t", "c", payload);
    }

    @Test
    void procesadoConExitoHaceAck() throws Exception {
        ack.handle("email", envelope(UUID.randomUUID(), new EmailPayload("email", "a@b.cl", "s", "b")),
                channel, 7L, () -> { });

        verify(channel).basicAck(7L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void envelopeInvalidoVaADlqSinReintentos() throws Exception {
        AtomicInteger calls = new AtomicInteger();

        ack.handle("email", envelope(null, null), channel, 8L, calls::incrementAndGet);

        verify(channel).basicNack(8L, false, false);
        org.assertj.core.api.Assertions.assertThat(calls.get()).isZero();
    }

    @Test
    void errorTransitorioSeReintentaYAlAgotarseVaADlq() throws Exception {
        AtomicInteger calls = new AtomicInteger();

        ack.handle("email", envelope(UUID.randomUUID(), new EmailPayload("email", "a@b.cl", "s", "b")),
                channel, 9L, () -> {
                    calls.incrementAndGet();
                    throw new IllegalStateException("bd caída");
                });

        org.assertj.core.api.Assertions.assertThat(calls.get()).isEqualTo(3);
        verify(channel).basicNack(9L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void datoInvalidoVaADlqSinReintentos() throws Exception {
        AtomicInteger calls = new AtomicInteger();

        ack.handle("email", envelope(UUID.randomUUID(), new EmailPayload("email", "", "s", "b")),
                channel, 11L, () -> {
                    calls.incrementAndGet();
                    throw new IllegalArgumentException("recipient vacío");
                });

        org.assertj.core.api.Assertions.assertThat(calls.get()).isEqualTo(1);
        verify(channel).basicNack(11L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void siElReintentoTieneExitoHaceAck() throws Exception {
        AtomicInteger calls = new AtomicInteger();

        ack.handle("email", envelope(UUID.randomUUID(), new EmailPayload("email", "a@b.cl", "s", "b")),
                channel, 10L, () -> {
                    if (calls.incrementAndGet() < 2) {
                        throw new IllegalStateException("transitorio");
                    }
                });

        verify(channel, times(1)).basicAck(10L, false);
    }
}
