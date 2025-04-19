package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Call;
import com.javanostra.spring.core.services.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/call")
public class CallController {
    @Autowired
    private CallService callService;

    @PostMapping("/start")
    public ResponseEntity<CallResponse> startCall(
            @RequestParam String callerId,
            @RequestParam String calleeId) {
        String callId = callService.initiateCall(callerId, calleeId);
        return ResponseEntity.ok(new CallResponse(callId));
    }

    @PostMapping("/end")
    public ResponseEntity<Void> endCall(@RequestParam String callId) {
        callService.endCall(callId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<List<ActiveCallDto>> getActiveCalls(@PathVariable String userId) {
        List<Call> calls = callService.findActiveCallsByUser(userId);
        return ResponseEntity.ok(calls.stream()
                .map(c -> new ActiveCallDto(c.getCallId(),
                    c.getCallerId().equals(userId) ? c.getCalleeId() : c.getCallerId()))
                .toList());
    }

    public record CallResponse(String callId) {}
    public record ActiveCallDto(String callId, String participantId) {}
}
