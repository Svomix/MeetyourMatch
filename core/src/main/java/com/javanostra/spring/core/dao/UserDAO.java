package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
    User findUserById(Long id);
    void deleteUserById(Long id);
}
