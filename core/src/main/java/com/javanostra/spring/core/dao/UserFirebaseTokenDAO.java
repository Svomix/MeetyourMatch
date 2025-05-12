package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.UserFirebaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFirebaseTokenDAO extends JpaRepository<UserFirebaseToken, Long> {

    List<UserFirebaseToken> findByUserId(Long userId);
    UserFirebaseToken findByUserIdAndDeviceId(Long userId, String deviceId);
}
