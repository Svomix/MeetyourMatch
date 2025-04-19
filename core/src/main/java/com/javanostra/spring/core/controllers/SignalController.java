package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.SignalingMessageDTO;
import com.javanostra.spring.core.services.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private CallService callService;

    @MessageMapping("/signal")
    public void handleSignal(@Payload SignalingMessageDTO message) {
        if (!callService.isCallActive(message.getId())) {
            throw new IllegalStateException("Call is not active");
        }
        messagingTemplate.convertAndSend(
                "/topic/signal/" + message.getRecipientId(),
                message
        );
    }
}
