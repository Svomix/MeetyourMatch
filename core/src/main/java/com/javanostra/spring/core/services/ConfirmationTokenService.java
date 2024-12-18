package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.ConfirmationTokenDAO;
import com.javanostra.spring.core.entities.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenDAO confirmationTokenDAO;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenDAO.save(confirmationToken);
    }

    public ConfirmationToken getConfirmationToken(Long userId) {
        return confirmationTokenDAO.findByUserId(userId);
    }

    public void deleteConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenDAO.delete(confirmationToken);
    }

    public void updateConfirmationToken(ConfirmationToken token) {
        confirmationTokenDAO.save(token);
    }
}
