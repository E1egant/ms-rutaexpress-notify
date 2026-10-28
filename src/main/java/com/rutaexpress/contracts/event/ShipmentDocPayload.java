package com.rutaexpress.contracts.event;

/** Payload de warehouse.ticket y label.gen. */
public record ShipmentDocPayload(String trackingNumber, String recipient) {
}
