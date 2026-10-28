package com.rutaexpress.contracts;

/**
 * Nombres canónicos de los recursos de mensajería. Los de RabbitMQ viven en
 * application.yml (rutaexpress.rabbit.*); aquí solo queda el topic de Kafka.
 */
public final class MessagingConstants {

    private MessagingConstants() {
    }

    public static final String SHIPMENT_EVENTS_TOPIC = "shipment-events";
}
