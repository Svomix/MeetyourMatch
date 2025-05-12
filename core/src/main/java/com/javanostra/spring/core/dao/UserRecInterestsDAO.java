package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.UserRecInterests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRecInterestsDAO extends JpaRepository<UserRecInterests, Long> {

    @Query("SELECT u FROM UserRecInterests u WHERE u.user_id = :userId AND u.interest = :interestName")
    UserRecInterests findByUserIdAndInterestName(@Param("userId") Long userId, @Param("interestName") String interestName);

    @Query("SELECT u FROM UserRecInterests u WHERE u.user_id = :userId")
    List<UserRecInterests> findAllByUserId(@Param("userId") Long userId);
}
