package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserAuthorityDAO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserAuthority;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    UserAuthorityDAO groupDAO;

    public static final String DEFAULT_AUTHORITY_NAME = "ROLE_USER";
    public static final String AUTHORITY_ADMIN_NAME= "ROLE_ADMIN";

    @Getter
    UserAuthority defaultGroup;

    public boolean HasAuthority(User user, String authority) {
        return user.getAuthorities().stream().anyMatch((a) -> a.getAuthority().equals(authority));
    }

    public boolean HasAdminAuthority(User user) {
        return HasAuthority(user, AUTHORITY_ADMIN_NAME);
    }

    void createAuthorityIfExists(String name){
        if(!groupDAO.existsByAuthority(name)){
            UserAuthority userGroup = new UserAuthority();
            userGroup.setAuthority(name);
            groupDAO.save(userGroup);
            groupDAO.flush();
        }
    }

    AuthorizationService(UserAuthorityDAO dao){
        groupDAO = dao;

        createAuthorityIfExists(DEFAULT_AUTHORITY_NAME);
        createAuthorityIfExists(AUTHORITY_ADMIN_NAME);

        defaultGroup = groupDAO.findByAuthority(DEFAULT_AUTHORITY_NAME);
    }
}
