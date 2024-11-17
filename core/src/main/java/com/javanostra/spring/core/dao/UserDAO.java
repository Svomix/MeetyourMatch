package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    User findUserById(Long id);
    void deleteUserById(Long id);
}
