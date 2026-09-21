package com.rutaexpress.notify.messaging;

import com.rutaexpress.contracts.event.NotificationRequest;
import com.rutaexpress.notify.config.RabbitConfig;
import com.rutaexpress.notify.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final NotificationService service;

    public NotificationListener(NotificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitConfig.NOTIFICATIONS_QUEUE)
    public void onNotification(NotificationRequest request) {
        service.process(request);
    }
}
