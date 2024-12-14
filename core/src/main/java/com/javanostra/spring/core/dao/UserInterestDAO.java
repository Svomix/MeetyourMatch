package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.UserInterest;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestDAO extends JpaRepository<UserInterest, Integer> {

}
