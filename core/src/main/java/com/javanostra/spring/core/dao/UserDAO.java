package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String username);
    boolean existsById(Long id);
    User findUserById(Long id);
    void deleteUserById(Long id);
    List<User> findAllByIsEnabledFalseAndCreatedAtBefore(LocalDateTime time);
    List<User> findALLByStatus(Status status);
}
