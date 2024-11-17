package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeDAO extends JpaRepository<Attribute, Long>, PagingAndSortingRepository<Attribute, Long> {
    Attribute findAttributeById(Long id);
    void deleteAttributeById(Long id);
}
