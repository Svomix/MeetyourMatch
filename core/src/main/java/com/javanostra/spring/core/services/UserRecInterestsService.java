package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserRecInterestsDAO;
import com.javanostra.spring.core.entities.UserRecInterests;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserRecInterestsService {
    private final UserRecInterestsDAO userRecInterestsDAO;

    public void save(UserRecInterests userRecInterests) {
        userRecInterestsDAO.save(userRecInterests);
    }

    public UserRecInterests findByIdAndName(Long id, String name) {
        return userRecInterestsDAO.findByUserIdAndInterestName(id, name);
    }

    public List<UserRecInterests> findAllById(Long id) {
        return userRecInterestsDAO.findAllByUserId(id);
    }
}
