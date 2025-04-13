package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersEventDAO extends JpaRepository<UserActions, Long>, PagingAndSortingRepository<UserActions, Long> {
    Page<UserActions> findAllUserEventsByUserId(Long userId, Pageable pageable);

    UserActions findUserEventByUserIdAndEventId(Long userId, Long eventId);
    UserActions findUserEventByUserAndEvent(User user, Event event);

    List<UserActions> findUserEventByEvent(Event event);

    List<UserActions> findUserEventByUserIdAndInCalendarIsTrue(Long userId);
    List<UserActions> findUserEventByUserAndInCalendarIsTrue(User user);

    void deleteUserEventByEventId(Long eventId);

    void deleteAllByUserId(Long userId);
}
