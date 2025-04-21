package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dto.FullUserProfileDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.dao.*;
import com.javanostra.spring.core.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<UserProfileDTO> findAllUsers(Pageable pageable, Specification<User> specification) {
        return userDAO.findAll(specification, pageable).map(u -> mapper.convertValue(u, UserProfileDTO.class));
    }

    public User findUserById(Long userId) {
        return userDAO.findUserById(userId);
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

    @Transactional
    public Page<UserProfileDTO> getFriends(User currentUser, Pageable pageable, String namePattern) {
        User user = userDAO.findUserById(currentUser.getId());
        List<User> friends = user.getFriends()
                .stream()
                .filter(u -> u.getUsername().contains(namePattern))
                .toList();
        return new PageImpl<>(
                friends,
                pageable,
                friends.size()
        ).map(a -> mapper.convertValue(a, UserProfileDTO.class));
    }

    @Transactional
    public void addFriend(User currentUser, User friend) {
        User user = userDAO.findUserById(currentUser.getId());
        user.getFriends().add(friend);
        userDAO.save(user);
    }

    @Transactional
    public void deleteFriend(User currentUser, User friend) {
        User user = userDAO.findUserById(currentUser.getId());
        user.getFriends().remove(friend);
        userDAO.save(user);
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
    public List<User> findConnectedUsers() {
        return userDAO.findALLByStatus(Status.ONLINE);
    }
}
