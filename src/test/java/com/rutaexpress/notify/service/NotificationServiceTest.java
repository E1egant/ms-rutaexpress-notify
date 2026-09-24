package com.rutaexpress.notify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rutaexpress.contracts.dto.NotificationDto;
import com.rutaexpress.contracts.event.NotificationRequest;
import com.rutaexpress.notify.domain.Notification;
import com.rutaexpress.notify.domain.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository repository;
    @InjectMocks
    NotificationService service;

    @Test
    void processGuardaLaNotificacionConLosDatosDelMensaje() {
        service.process(new NotificationRequest(UUID.randomUUID(), "EMAIL", "ana@example.com", "Envio aceptado", "Tu envio fue aceptado"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getChannel()).isEqualTo("EMAIL");
        assertThat(captor.getValue().getRecipient()).isEqualTo("ana@example.com");
        assertThat(captor.getValue().getSubject()).isEqualTo("Envio aceptado");
        assertThat(captor.getValue().getBody()).isEqualTo("Tu envio fue aceptado");
    }

    @Test
    void processDescartaUnMensajeYaProcesado() {
        UUID messageId = UUID.randomUUID();
        when(repository.existsByMessageId(messageId)).thenReturn(true);

        service.process(new NotificationRequest(messageId, "EMAIL", "ana@example.com", "s", "b"));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void listDevuelveLasNotificacionesComoDto() {
        Notification n = new Notification();
        n.setChannel("PUSH");
        n.setRecipient("bodega");
        n.setSubject("Ticket");
        n.setBody("Preparar envio 1");
        when(repository.findAll()).thenReturn(List.of(n));

        assertThat(service.list()).extracting(NotificationDto::channel, NotificationDto::recipient)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("PUSH", "bodega"));
    }
}
