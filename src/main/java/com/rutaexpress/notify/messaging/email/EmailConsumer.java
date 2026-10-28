package com.rutaexpress.notify.messaging.email;

import com.rabbitmq.client.Channel;
import com.rutaexpress.contracts.event.EmailPayload;
import com.rutaexpress.contracts.event.MessageEnvelope;
import com.rutaexpress.notify.messaging.AckSupport;
import com.rutaexpress.notify.service.NotificationService;
import java.io.IOException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final NotificationService service;
    private final AckSupport ack;

    public EmailConsumer(NotificationService service, AckSupport ack) {
        this.service = service;
        this.ack = ack;
    }

    @RabbitListener(queues = "${rutaexpress.rabbit.queues.email}")
    public void onEmail(MessageEnvelope<EmailPayload> envelope, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        ack.handle("email", envelope, channel, tag, () -> {
            EmailPayload p = envelope.payload();
            service.process(envelope.eventId(), p.channel(), p.recipient(), p.subject(), p.body());
        });
    }
}
