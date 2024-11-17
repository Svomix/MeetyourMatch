package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.EventAttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventAttributeValueDAO extends JpaRepository<EventAttributeValue, Long>, PagingAndSortingRepository<EventAttributeValue, Long> {
    List<EventAttributeValue> findByEvent(Event event);
    void deleteByEventAndAttributeId(Event event, Long id);
}
