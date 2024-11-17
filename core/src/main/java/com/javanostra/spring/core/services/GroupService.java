package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserGroupDAO;
import com.javanostra.spring.core.entities.UserAuthority;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    UserGroupDAO groupDAO;

    static final String DEFAULT_AUTHORITY_NAME = "USER";

    @Getter
    UserAuthority defaultGroup;

    GroupService(UserGroupDAO dao){
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
