package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dao.UserInterestDAO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserInterest;
import com.javanostra.spring.core.exceptions.InterestAlreadyPresentException;
import com.javanostra.spring.core.exceptions.InterestNotPresentException;
import com.javanostra.spring.core.exceptions.NoSuchInterestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InterestService {
    private final UserDAO userDAO;
    private final UserInterestDAO userInterestDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Set<UserInterest> getUserInterests(User user){
        user = userDAO.findUserById(user.getId()); // TODO: find better fix
        Hibernate.initialize(user.getInterests());
        return user.getInterests();
    }

    @Transactional
    public void addUserInterest(User user, UserInterest userInterest) throws InterestAlreadyPresentException {
        user = userDAO.findUserById(user.getId());
        if(user.getInterests().contains(userInterest)) throw new InterestAlreadyPresentException("Такой интерес уже присутсвует");
        user.getInterests().add(userInterest);
        userDAO.save(user);
    }

    @Transactional
    public void removeUserInterest(User user, UserInterest userInterest) throws InterestNotPresentException {
        user = userDAO.findUserById(user.getId());
        if(!user.getInterests().contains(userInterest)) throw new InterestNotPresentException("Такой интерес отсутсвует");
        user.getInterests().remove(userInterest);
        userDAO.save(user);
    }

    @Transactional
    public void saveInterest(UserInterest userInterest){
        userInterestDAO.save(userInterest);
    }

    public UserInterest getUserInterestById(int id) throws NoSuchInterestException {
        return userInterestDAO.findById(id).orElseThrow(() -> new NoSuchInterestException("Такого интереса не существует"));
    }

    public Set<UserInterest> getAllUserInterests(){
        return new HashSet<>(userInterestDAO.findAll());
    }


}
