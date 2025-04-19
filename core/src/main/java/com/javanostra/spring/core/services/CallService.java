package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.CallDAO;
import com.javanostra.spring.core.entities.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CallService {
    @Autowired
    private CallDAO callRepository;

    public String initiateCall(String callerId, String calleeId) {
        String callId = "call-" + UUID.randomUUID();

        Call call = new Call();
        call.setCallId(callId);
        call.setCallerId(callerId);
        call.setCalleeId(calleeId);
        call.setStartedAt(Instant.now());
        call.setStatus(Call.CallStatus.ACTIVE);

        callRepository.save(call);
        return callId;
    }

    public void endCall(String callId) {
        Call call = callRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setEndedAt(Instant.now());
        call.setStatus(Call.CallStatus.ENDED);
        callRepository.save(call);
    }

    public boolean isCallActive(String callId) {
        return callRepository.findByCallId(callId)
                .map(c -> c.getStatus() == Call.CallStatus.ACTIVE)
                .orElse(false);
    }
    public List<Call> findActiveCallsByUser(String userId) {
        return callRepository.findActiveCallsByUser(userId);
    }
}