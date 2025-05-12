package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserFirebaseTokenDAO;
import com.javanostra.spring.core.entities.UserFirebaseToken;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserFirebaseTokenService {
    private final UserFirebaseTokenDAO userFirebaseTokenDAO;

    @Transactional
    public void save(UserFirebaseToken userFirebaseToken) {
        userFirebaseTokenDAO.save(userFirebaseToken);
    }

    public UserFirebaseToken findByUserIdAndDeviceId(Long userId, String deviceId) {
        return userFirebaseTokenDAO.findByUserIdAndDeviceId(userId, deviceId);
    }


    @Transactional
    public void delete(UserFirebaseToken userFirebaseToken) {
        userFirebaseTokenDAO.delete(userFirebaseToken);
    }
}
