package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersAttributeValueDAO extends JpaRepository<UserAttribute, Long>, PagingAndSortingRepository<UserAttribute, Long> {
    void deleteAllByUserId(Long userId);
}
