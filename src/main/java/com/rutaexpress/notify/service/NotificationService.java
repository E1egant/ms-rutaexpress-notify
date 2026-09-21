package com.rutaexpress.notify.service;

import com.rutaexpress.contracts.dto.NotificationDto;
import com.rutaexpress.contracts.event.NotificationRequest;
import com.rutaexpress.notify.domain.Notification;
import com.rutaexpress.notify.domain.NotificationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void process(NotificationRequest request) {
        Notification notification = new Notification();
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
