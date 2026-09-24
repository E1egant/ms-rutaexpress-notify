package com.rutaexpress.notify.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByMessageId(UUID messageId);
}
