package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dao.UsersEventDAO;
import com.javanostra.spring.core.dto.EventDTO;
import com.javanostra.spring.core.dto.UserActionDTO;
import com.javanostra.spring.core.dto.UserActionEDTO;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserActions;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserActionsService {
    private final UserService userService;
    private final EventService eventService;
    private final UsersEventDAO usersEventDAO;

    ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    public Page<UserActions> findAllUserEvents(Long userId, Pageable pageable) {
        return usersEventDAO.findAllUserEventsByUserId(userId, pageable);
    }

    public List<UserActionDTO> findUserEventsInCalendar(User user) {
        List<UserActions> userEvents = usersEventDAO.findUserEventByUserAndInCalendarIsTrue(user);
        return userEvents.stream()
                .map(event -> {
                    UserActionDTO userActionDTO = mapper.convertValue(event, UserActionDTO.class);
                    userActionDTO.setEvent(mapper.convertValue(event.getEvent(), EventDTO.class));
                    return userActionDTO;
                })
                .toList();
    }

    public Integer getLikes(Event event) {
        List<UserActions> userEvents = usersEventDAO.findUserEventByEvent(event);
        int counter = 0;
        for (UserActions userEvent : userEvents) {
            if (userEvent.getIsLiked()) counter++;
        }
        return counter;
    }

    @Transactional
    public UserActionDTO setLiked(User user, Event event, boolean like) {
        user = userService.findUserById(user.getId());
        UserActions userEvent = usersEventDAO.findUserEventByUserAndEvent(user, event);
        if (userEvent == null) {
            userEvent = new UserActions();
            userEvent.setUser(user);
            userEvent.setEvent(event);
            userEvent.setIsDisliked(false);
            userEvent.setInCalendar(false);
        }
        userEvent.setIsLiked(like);
        usersEventDAO.save(userEvent);
        UserActionDTO userActionDTO = mapper.convertValue(userEvent, UserActionDTO.class);
        userActionDTO.setEvent(mapper.convertValue(event, EventDTO.class));
        return userActionDTO;
    }

    @Transactional
    public UserActionDTO setDisliked(User user, Event event, boolean dislike) {
        user = userService.findUserById(user.getId());
        UserActions userEvent = usersEventDAO.findUserEventByUserAndEvent(user, event);
        if (userEvent == null) {
            userEvent = new UserActions();
            userEvent.setUser(user);
            userEvent.setEvent(event);
            userEvent.setIsLiked(false);
            userEvent.setInCalendar(false);
        }
        userEvent.setIsDisliked(dislike);
        usersEventDAO.save(userEvent);
        UserActionDTO userActionDTO = mapper.convertValue(userEvent, UserActionDTO.class);
        userActionDTO.setEvent(mapper.convertValue(event, EventDTO.class));
        return userActionDTO;
    }

    @Transactional
    public UserActionDTO setCalendar(User user, Event event, boolean calendar) {
        user = userService.findUserById(user.getId());
        UserActions userEvent = usersEventDAO.findUserEventByUserAndEvent(user, event);
        if (userEvent == null) {
            userEvent = new UserActions();
            userEvent.setUser(user);
            userEvent.setEvent(event);
            userEvent.setIsLiked(false);
            userEvent.setIsDisliked(false);
        }
        userEvent.setInCalendar(calendar);
        usersEventDAO.save(userEvent);
        UserActionDTO userActionDTO = mapper.convertValue(userEvent, UserActionDTO.class);
        userActionDTO.setEvent(mapper.convertValue(event, EventDTO.class));
        return userActionDTO;
    }

    public UserActionDTO getUserEventActions(User user, Event event) {
        UserActions userEvent = usersEventDAO.findUserEventByUserAndEvent(user, event);
        if (Objects.isNull(userEvent)) {
            return new UserActionDTO(mapper.convertValue(event, EventDTO.class),false, false, false);
        }
        return mapper.convertValue(userEvent, UserActionDTO.class);
    }

    @Transactional
    public UserActionEDTO getUserEventActionsE(Event event, User user) {
        UserActions userEvent = usersEventDAO.findUserEventByUserAndEvent(user, event);
        if (Objects.isNull(userEvent)) {
            return new UserActionEDTO();
        }
        return mapper.convertValue(userEvent, UserActionEDTO.class);
    }

    public UserActions findUserEventById(Long userId, Long eventId) {
        return usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
    }

    @Transactional
    public Page<EventDTO> populateUserActions(Page<Event> events, User user) {
        List<UserActions> userActions = usersEventDAO.findUserEventsByUserAndEventIn(user, events.toList());
        return events.map(a -> {
            EventDTO dto = mapper.convertValue(a, EventDTO.class);
            dto.setUserAction(userActions.stream()
                    .filter((e) -> e.getEvent().getId().equals(a.getId()))
                    .findFirst()
                    .map((e) -> mapper.convertValue(e, UserActionEDTO.class))
                    .orElseGet(UserActionEDTO::new));
            return dto;
        });
    }
}
