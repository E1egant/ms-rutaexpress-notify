package com.rutaexpress.notify.messaging.warehouse;

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
public class WarehouseConsumer {

    private final NotificationService service;
    private final AckSupport ack;

    public WarehouseConsumer(NotificationService service, AckSupport ack) {
        this.service = service;
        this.ack = ack;
    }

    @RabbitListener(queues = "${rutaexpress.rabbit.queues.warehouse}")
    public void onTicket(MessageEnvelope<ShipmentDocPayload> envelope, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        ack.handle("warehouse", envelope, channel, tag, () -> {
            ShipmentDocPayload p = envelope.payload();
            service.process(envelope.eventId(), "warehouse", p.recipient(), "Ticket de bodega", "Ticket generado para el envío " + p.trackingNumber());
        });
    }
}
