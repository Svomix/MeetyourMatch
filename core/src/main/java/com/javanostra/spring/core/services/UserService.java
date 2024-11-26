package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.dao.*;
import com.javanostra.spring.core.dto.UserEventDTO;
import com.javanostra.spring.core.entities.*;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import com.javanostra.spring.core.dao.AttributeDAO;
import com.javanostra.spring.core.dao.UsersAttributeValueDAO;
import com.javanostra.spring.core.dao.UsersEventDAO;
import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.entities.UserAttributeValue;
import com.javanostra.spring.core.entities.UserEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    @NonNull
    private final UserDAO userDAO;
    @NonNull
    private final UsersEventDAO usersEventDAO;
    @NonNull
    private final EventDAO eventDAO;
    @NonNull
    private final UsersAttributeValueDAO usersAttributeValueDAO;
    @NonNull
    private final AttributeDAO attributeDAO;
    @NonNull
    private final TagDAO tagDAO;

    AuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @Transactional
    @Override
    public void createUser(UserDetails user) {
        userDAO.save((User) user);
    }

    @Transactional
    @Override
    public void updateUser(UserDetails user) {
        //User user_ent = userDAO.findByUsername(user.getUsername());
        //user_ent.setPassword(user.getPassword());
        //user_ent.setAuthorities( userGroupDAO.findByAuthorityIn(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) );
        userDAO.save((User) user);
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        User user_ent = userDAO.findByUsername(username);
        userDAO.delete(user_ent);
    }

    @Transactional
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = this.loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, (Object) null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userDAO.existsByUsernameIgnoreCase(username);
    }

    public boolean userExistsByEmail(String email) {
        return userDAO.existsByEmailIgnoreCase(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user_ent = userDAO.findByUsername(username);
        if (Objects.nonNull(user_ent)) {
            return user_ent;
        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userDAO.findAll(pageable);
    }

    public User findUserById(Long userId) {
        return userDAO.findUserById(userId);
    }

    public Page<UserEvent> findAllUserEvents(Long userId, Pageable pageable) {
        return usersEventDAO.findAllUserEventsByUserId(userId, pageable);
    }

    public List<UserEventDTO> findUserEventsInCalendar(Long userId) {
        List<UserEvent> userEvents = usersEventDAO.findUserEventByUserIdAndInCalendarIsTrue(userId);
        return userEvents.stream()
                .map(event -> new UserEventDTO(
                        event.getUser().getId(),
                        event.getEvent().getId(),
                        event.getIsLiked(),
                        event.getIsDisliked(),
                        event.getInCalendar()
                ))
                .toList();
    }

    public Integer getLikes(Long eventId) {
        List<UserEvent> userEvents = usersEventDAO.findUserEventByEvent(eventDAO.findEventById(eventId));
        int counter = 0;
        for (UserEvent userEvent : userEvents) {
            if (userEvent.getIsLiked()) counter++;
        }
        return counter;
    }

    @Transactional
    public void switchLiked(Long userId, Long eventId) {
        UserEvent userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserEvent();
            userEvent.setUser(userDAO.findUserById(userId));
            userEvent.setEvent(eventDAO.findEventById(eventId));
            userEvent.setIsDisliked(false);
            userEvent.setInCalendar(false);
        }
        userEvent.setIsLiked(!userEvent.getIsLiked());
        usersEventDAO.save(userEvent);
    }

    @Transactional
    public void switchDisliked(Long userId, Long eventId) {
        UserEvent userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserEvent();
            userEvent.setUser(userDAO.findUserById(userId));
            userEvent.setEvent(eventDAO.findEventById(eventId));
            userEvent.setIsLiked(false);
            userEvent.setInCalendar(false);
        }
        userEvent.setIsDisliked(!userEvent.getIsDisliked());
        usersEventDAO.save(userEvent);
    }

    @Transactional
    public void switchCalendar(Long userId, Long eventId) {
        UserEvent userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserEvent();
            userEvent.setUser(userDAO.findUserById(userId));
            userEvent.setEvent(eventDAO.findEventById(eventId));
            userEvent.setIsLiked(false);
            userEvent.setIsDisliked(false);
        }
        userEvent.setInCalendar(!userEvent.getInCalendar());
        usersEventDAO.save(userEvent);
    }

    public UserEventDTO getUserEventByIds(Long userId, Long eventId) {
        UserEvent userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserEvent();
            userEvent.setUser(userDAO.findUserById(userId));
            userEvent.setEvent(eventDAO.findEventById(eventId));
            userEvent.setIsLiked(false);
            userEvent.setIsDisliked(false);
            userEvent.setInCalendar(false);
        } else {
            return new UserEventDTO(userEvent.getUser().getId(), userEvent.getEvent().getId(), userEvent.getIsLiked(), userEvent.getIsDisliked(), userEvent.getInCalendar());
        }
        usersEventDAO.save(userEvent);
        return new UserEventDTO(userEvent.getUser().getId(), userEvent.getEvent().getId(), userEvent.getIsLiked(), userEvent.getIsDisliked(), userEvent.getInCalendar());
    }

    public UserEvent findUserEventById(Long userId, Long eventId) {
        return usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
    }

    public Page<Attribute> findUserAttributesByUserId(Long userId, Pageable pageable) {
        List<Attribute> attributeList = usersAttributeValueDAO
                .findByUser(userDAO.findUserById(userId))
                .stream()
                .map(UserAttributeValue::getAttribute)
                .toList();

        final int start = (int) pageable.getOffset();
        final int end = Math.min((int) pageable.getOffset() + pageable.getPageSize(), attributeList.size());
        return new PageImpl<>(attributeList.subList(start, end), pageable, attributeList.size());
    }

    public List<Tag> findUserTagsByUserId(Long userId) {
        List<UserAttributeValue> UAVs = usersAttributeValueDAO.findByUserAndAttribute(userDAO.findUserById(userId), attributeDAO.findAttributeById(1L));
        List<Tag> tags = new ArrayList<>();
        for (UserAttributeValue UAV : UAVs) {
            tagDAO.findById(Long.parseLong(UAV.getValue())).ifPresent(tags::add);
        }
        return tags;
    }

    public List<UserAttributeValue> findUserAttributeValueByUserIdAndAttributeId(Long userId, Long attributeId) {
        return usersAttributeValueDAO.findByUserAndAttribute(userDAO.findUserById(userId), attributeDAO.findAttributeById(attributeId));
    }

//    @Transactional
//    public void saveUser(User user) {
//        userDAO.save(user);
//    }

    @Transactional
    public void saveUserEvent(UserEvent event) {
        usersEventDAO.save(event);
    }

//    @Transactional
//    public void updateUser(User user) {
//        userDAO.save(user);
//    }

    @Transactional
    public void updateUserEvent(UserEvent event) {
        usersEventDAO.save(event);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        userDAO.deleteUserById(userId);
    }

    @Transactional
    public void deleteUserEventById(Long eventId) {
        usersEventDAO.deleteUserEventByEventId(eventId);
    }

    @Transactional
    public void createUserAttributeValue(Long userId, Long attrId, String value) {
        UserAttributeValue userAttributeValue = new UserAttributeValue();
        userAttributeValue.setUser(userDAO.findUserById(userId));
        userAttributeValue.setAttribute(attributeDAO.findAttributeById(attrId));
        userAttributeValue.setValue(value);
        usersAttributeValueDAO.save(userAttributeValue);
    }

    @Transactional
    public void deleteUserAttributeByAttrId(Long userId, Long attrId) {
        usersAttributeValueDAO.deleteByUserAndAttributeId(userDAO.findUserById(userId), attrId);
    }

    @Transactional
    public void deleteUAVByUserAndAttributeAndValue(Long userId, Long attrId, String value) {
        usersAttributeValueDAO.deleteByUserIdAndAttributeIdAndValue(userId, attrId, value);
    }

    public User getCurrentUser() {
        SecurityContext ctx = securityContextHolderStrategy.getContext();
        if (Objects.nonNull(ctx)) {
            Authentication authentication = ctx.getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof User user) {
                return user;
            }
        }
        return null;
    }
}
