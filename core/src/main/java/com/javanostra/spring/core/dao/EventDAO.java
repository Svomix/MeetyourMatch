package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDAO extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {
}
