package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dao.UserAuthorityDAO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    @NonNull
    UserDAO userDAO;
    @NonNull
    private final UsersEventDAO usersEventDAO;
    @NonNull
    private final UsersAttributeValueDAO usersAttributeValueDAO;
    @NonNull
    private final AttributeDAO attributeDAO;

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
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException { //pls fix
        Authentication currentUser = securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        } else {
            String username = currentUser.getName();
            if (this.authenticationManager != null) {
                //this.logger.debug(LogMessage.format("Reauthenticating user '%s' for password change request.", username));
                this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
            } else {
                //this.logger.debug("No authentication manager set. Password won't be re-checked.");
            }

            //this.logger.debug("Changing password for user '" + username + "'");
            //this.getJdbcTemplate().update(this.changePasswordSql, new Object[]{newPassword, username});
            Authentication authentication = this.createNewAuthentication(currentUser, newPassword);
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            this.securityContextHolderStrategy.setContext(context);
            //this.userCache.removeUserFromCache(username);
        }
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = this.loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, (Object) null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userDAO.existsByUsername(username);
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

    public User getCurrentUser() {
        SecurityContext ctx = securityContextHolderStrategy.getContext();
        if(Objects.nonNull(ctx)) {
            Authentication authentication = ctx.getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof User user) {
                return user;
            }
        }
        return null;
    }
}
