package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagDAO extends JpaRepository<Tag, Long> {
    public Optional<Tag> findByNameIgnoreCase(String name);
}
