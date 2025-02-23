package com.group17.comic.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group17.comic.dtos.request.ClientToken;
import com.group17.comic.enums.GlobalStorage;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/notification")
@RequiredArgsConstructor
@Tag(name = "Notification")
public class NotificationController {
    private final GlobalStorage globalStorage;

    @PostMapping("/fcm/token")
    public void setClientToken(@RequestBody ClientToken token) {
        globalStorage.put("clientToken", token.token());
    }
}
