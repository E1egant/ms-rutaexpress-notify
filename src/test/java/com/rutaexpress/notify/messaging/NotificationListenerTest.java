package com.rutaexpress.notify.messaging;

import static org.mockito.Mockito.verify;

import com.rutaexpress.contracts.event.NotificationRequest;
import com.rutaexpress.notify.service.NotificationService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    NotificationService service;
    @InjectMocks
    NotificationListener listener;

    @Test
    void elMensajeDeLaColaSeProcesaEnElServicio() {
        NotificationRequest request = new NotificationRequest(UUID.randomUUID(), "EMAIL", "a@b.cl", "s", "b");

        listener.onNotification(request);

        verify(service).process(request);
    }
}
