package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.AttributeDAO;
import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.dao.UsersAttributeValueDAO;
import com.javanostra.spring.core.dao.UsersEventDAO;
import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserAttributeValue;
import com.javanostra.spring.core.entities.UserEvent;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    private final UsersEventDAO usersEventDAO;
    private final UsersAttributeValueDAO usersAttributeValueDAO;
    private final AttributeDAO attributeDAO;

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

    @Transactional
    public void saveUser(User user) {
        userDAO.save(user);
    }

    @Transactional
    public void saveUserEvent(UserEvent event) {
        usersEventDAO.save(event);
    }

    @Transactional
    public void updateUser(User user) {
        userDAO.save(user);
    }

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
}
