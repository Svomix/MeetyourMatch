package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserAuthorityDAO;
import com.javanostra.spring.core.entities.UserAuthority;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    UserAuthorityDAO groupDAO;

    static final String DEFAULT_AUTHORITY_NAME = "ROLE_USER";

    @Getter
    UserAuthority defaultGroup;

    GroupService(UserAuthorityDAO dao){
        groupDAO = dao;

        if(!groupDAO.existsByAuthority(DEFAULT_AUTHORITY_NAME)){
            UserAuthority userGroup = new UserAuthority();
            userGroup.setAuthority(DEFAULT_AUTHORITY_NAME);
            groupDAO.save(userGroup);
            groupDAO.flush();
        }
        defaultGroup = groupDAO.findByAuthority(DEFAULT_AUTHORITY_NAME);
    }
}
