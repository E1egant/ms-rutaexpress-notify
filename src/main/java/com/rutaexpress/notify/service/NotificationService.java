package com.rutaexpress.notify.service;

import com.rutaexpress.contracts.dto.NotificationDto;
import com.rutaexpress.notify.domain.Notification;
import com.rutaexpress.notify.domain.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void process(UUID eventId, String channel, String recipient, String subject, String body) {
        if (eventId != null && repository.existsByMessageId(eventId)) {
            log.info("Mensaje {} ya procesado, se descarta el reintento", eventId);
            return;
        }
        Notification notification = new Notification();
        notification.setMessageId(eventId);
        notification.setChannel(channel);
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setBody(body);
        repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> list() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getChannel(),
                notification.getRecipient(), notification.getSubject(),
                notification.getBody(), notification.getCreatedAt());
    }
}
