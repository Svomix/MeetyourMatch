package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface UsersAttributeValueDAO extends JpaRepository<UserAttributeValue, Long>, PagingAndSortingRepository<UserAttributeValue, Long> {
    void deleteByUserAndAttributeId(User user, Long attributeId);
    void deleteByUserIdAndAttributeIdAndValue(Long userId, Long attributeId, String value);
    List<UserAttributeValue> findByUser(User user);
    List<UserAttributeValue> findByUserAndAttribute(User user, Attribute attribute);
}
