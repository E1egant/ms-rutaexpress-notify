package com.rutaexpress.contracts.event;

public record EmailPayload(String channel, String recipient, String subject, String body) {
}
