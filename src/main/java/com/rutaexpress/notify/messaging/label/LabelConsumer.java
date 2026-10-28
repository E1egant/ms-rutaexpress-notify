package com.rutaexpress.notify.messaging.label;

import com.rabbitmq.client.Channel;
import com.rutaexpress.contracts.event.ShipmentDocPayload;
import com.rutaexpress.contracts.event.MessageEnvelope;
import com.rutaexpress.notify.messaging.AckSupport;
import com.rutaexpress.notify.service.NotificationService;
import java.io.IOException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class LabelConsumer {

    private final NotificationService service;
    private final AckSupport ack;

    public LabelConsumer(NotificationService service, AckSupport ack) {
        this.service = service;
        this.ack = ack;
    }

    @RabbitListener(queues = "${rutaexpress.rabbit.queues.label}")
    public void onLabel(MessageEnvelope<ShipmentDocPayload> envelope, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        ack.handle("label", envelope, channel, tag, () -> {
            ShipmentDocPayload p = envelope.payload();
            service.process(envelope.eventId(), "label", p.recipient(), "Etiqueta de envío", "Etiqueta generada para el envío " + p.trackingNumber());
        });
    }
}
