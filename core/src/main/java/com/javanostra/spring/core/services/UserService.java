package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dto.FullUserProfileDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.dao.*;
import com.javanostra.spring.core.dto.UserEventDTO;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.javanostra.spring.core.dao.UsersAttributeValueDAO;
import com.javanostra.spring.core.dao.UsersEventDAO;
import com.javanostra.spring.core.entities.UserAttribute;
import com.javanostra.spring.core.entities.UserActions;

import java.time.LocalDateTime;
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
    private final TagDAO tagDAO;
    @NonNull
    private final ConfirmationTokenDAO tokenDAO;

    AuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    ObjectMapper mapper = new ObjectMapper();

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
    public void delete(User user) {
        usersEventDAO.deleteAllByUserId(user.getId());
        usersAttributeValueDAO.deleteAllByUserId(user.getId());
        tokenDAO.deleteAllByUserId(user.getId());
        userDAO.delete(user);
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

    public Page<UserActions> findAllUserEvents(Long userId, Pageable pageable) {
        return usersEventDAO.findAllUserEventsByUserId(userId, pageable);
    }

    public List<UserEventDTO> findUserEventsInCalendar(Long userId) {
        List<UserActions> userEvents = usersEventDAO.findUserEventByUserIdAndInCalendarIsTrue(userId);
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
        List<UserActions> userEvents = usersEventDAO.findUserEventByEvent(eventDAO.findEventById(eventId));
        int counter = 0;
        for (UserActions userEvent : userEvents) {
            if (userEvent.getIsLiked()) counter++;
        }
        return counter;
    }

    @Transactional
    public void switchLiked(Long userId, Long eventId) {
        UserActions userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserActions();
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
        UserActions userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserActions();
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
        UserActions userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserActions();
            userEvent.setUser(userDAO.findUserById(userId));
            userEvent.setEvent(eventDAO.findEventById(eventId));
            userEvent.setIsLiked(false);
            userEvent.setIsDisliked(false);
        }
        userEvent.setInCalendar(!userEvent.getInCalendar());
        usersEventDAO.save(userEvent);
    }

    public UserEventDTO getUserEventByIds(Long userId, Long eventId) {
        UserActions userEvent = usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
        if (userEvent == null) {
            userEvent = new UserActions();
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

    public UserActions findUserEventById(Long userId, Long eventId) {
        return usersEventDAO.findUserEventByUserIdAndEventId(userId, eventId);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Transactional
    public void saveUserEvent(UserActions event) {
        usersEventDAO.save(event);
    }

    @Transactional
    public void updateUserEvent(UserActions event) {
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
    public void createUserAttributeValue(Long userId, String attribute, String value) {
        UserAttribute userAttributeValue = new UserAttribute();
        userAttributeValue.setUser(userDAO.findUserById(userId));
        userAttributeValue.setAttribute(attribute);
        userAttributeValue.setValue(value);
        usersAttributeValueDAO.save(userAttributeValue);
    }

//    @Deprecated
//    public List<UserAttribute> getAttributes(User user){
//        return usersAttributeValueDAO.findByUser(user);
//    }
//
//    public List<UserAttribute> getAttributes(User user, String attribute){
//        return usersAttributeValueDAO.findByUserAndAttribute(user, attribute);
//    }

    @Transactional
    public void AddAttribute(User user, String attribute, String value){
        UserAttribute userAttribute = new UserAttribute();
        userAttribute.setUser(user);
        userAttribute.setValue(value);
        userAttribute.setAttribute(attribute);
        usersAttributeValueDAO.save(userAttribute);
    }

    public FullUserProfileDTO getFullUserInfo(User user){
        return mapper.convertValue(user, FullUserProfileDTO.class);
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

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    @Async
    public void deleteUnconfirmedAccounts() {
        LocalDateTime filterTime = LocalDateTime.now().minusHours(2);
        List<User> unconfirmedUsers = userDAO.findAllByIsEnabledFalseAndCreatedAtBefore(filterTime);
        for (User user : unconfirmedUsers) {
            delete(user);
        }
    }
}
