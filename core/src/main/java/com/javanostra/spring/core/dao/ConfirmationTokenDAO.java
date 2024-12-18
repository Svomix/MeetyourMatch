package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenDAO extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByUserId(Long userId);
}
