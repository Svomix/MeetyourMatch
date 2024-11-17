package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.UserEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersEventDAO extends JpaRepository<UserEvent, Long>, PagingAndSortingRepository<UserEvent, Long> {
    Page<UserEvent> findAllUserEventsByUserId(Long userId, Pageable pageable);

    UserEvent findUserEventByUserIdAndEventId(Long userId, Long eventId);

    void deleteUserEventByEventId(Long eventId);
}
