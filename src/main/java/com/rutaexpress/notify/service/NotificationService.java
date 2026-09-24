package com.rutaexpress.notify.service;

import com.rutaexpress.contracts.dto.NotificationDto;
import com.rutaexpress.contracts.event.NotificationRequest;
import com.rutaexpress.notify.domain.Notification;
import com.rutaexpress.notify.domain.NotificationRepository;
import java.util.List;
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
    public void process(NotificationRequest request) {
        if (request.id() != null && repository.existsByMessageId(request.id())) {
            log.info("Notificación {} ya procesada, se descarta el reintento", request.id());
            return;
        }
        Notification notification = new Notification();
        notification.setMessageId(request.id());
        notification.setChannel(request.channel());
        notification.setRecipient(request.recipient());
        notification.setSubject(request.subject());
        notification.setBody(request.body());
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
