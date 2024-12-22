package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String username);
    User findUserById(Long id);
    void deleteUserById(Long id);
    List<User> findAllByIsEnabledFalseAndCreatedAtBefore(LocalDateTime time);
}
