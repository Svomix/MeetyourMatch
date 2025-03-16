package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.EventComment;
import com.javanostra.spring.core.entities.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentDAO extends JpaRepository<EventComment, Integer> {

}
