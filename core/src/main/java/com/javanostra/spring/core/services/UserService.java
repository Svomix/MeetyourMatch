package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dao.UserGroupDAO;
import com.javanostra.spring.core.entities.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    @NonNull
    UserDAO userDAO;
    @NonNull
    UserGroupDAO userGroupDAO;
    AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @Override
    public void createUser(UserDetails user) {
        userDAO.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        //User user_ent = userDAO.findByUsername(user.getUsername());
        //user_ent.setPassword(user.getPassword());
        //user_ent.setAuthorities( userGroupDAO.findByAuthorityIn(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) );
        userDAO.save((User) user);
    }

    @Override
    public void deleteUser(String username) {
        User user_ent = userDAO.findByUsername(username);
        userDAO.delete(user_ent);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
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
        if(Objects.nonNull(user_ent)){
            return user_ent;
        }else{
            throw new UsernameNotFoundException("username not found");
        }
    }
}
