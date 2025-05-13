package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.dto.UserActionCountersDTO;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UsersEventDAO extends JpaRepository<UserActions, Long>, PagingAndSortingRepository<UserActions, Long> {
    Page<UserActions> findAllUserEventsByUserId(Long userId, Pageable pageable);

    UserActions findUserEventByUserIdAndEventId(Long userId, Long eventId);
    UserActions findUserEventByUserAndEvent(User user, Event event);
    List<UserActions> findUserEventsByUserAndEventIn(User user, Collection<Event> events);

    @Query("select new com.javanostra.spring.core.dto.UserActionCountersDTO(count(*) FILTER (WHERE a.isLiked)," +
            "count(*) FILTER (WHERE a.isDisliked)," +
            "count(*) FILTER (WHERE a.inCalendar)) from UserActions a where a.event=?1")
    UserActionCountersDTO countActionsByEvent(Event event);

    List<UserActions> findUserEventByEvent(Event event);

    @Query("select a.user from UserActions a where a.inCalendar")
    List<User> findUsersByEventInCalendar(Event event);

    List<UserActions> findUserEventByUserIdAndInCalendarIsTrue(Long userId);
    List<UserActions> findUserEventByUserAndInCalendarIsTrue(User user);

    void deleteUserEventByEventId(Long eventId);

    void deleteAllByUserId(Long userId);
}
