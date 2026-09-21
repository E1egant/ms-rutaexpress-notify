package com.rutaexpress.notify.web;

import com.rutaexpress.contracts.ApiPaths;
import com.rutaexpress.contracts.dto.NotificationDto;
import com.rutaexpress.notify.service.NotificationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationDto> list() {
        return service.list();
    }
}
