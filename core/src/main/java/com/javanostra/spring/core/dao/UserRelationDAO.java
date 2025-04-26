package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRelationDAO extends JpaRepository<UserRelation, Long> {

    @Query(value = """
    SELECT u FROM User u
    WHERE u.id IN (
        SELECT ur.friendId FROM UserRelation ur
        WHERE ur.userId = :userId AND ur.isAccepted = true
    )
    OR u.id IN (
        SELECT ur.userId FROM UserRelation ur
        WHERE ur.friendId = :userId AND ur.isAccepted = true
    )""")
    List<User> findFriendsByUserId(@Param("userId") Long userId);

    @Query(value = """
    SELECT u FROM User u
    WHERE u.id IN (
        SELECT ur.userId FROM UserRelation ur
        WHERE ur.friendId = :friendId AND ur.isAccepted = false
    )""")
    List<User> findAllSendersByFriendId(@Param("friendId") Long friendId);

    @Query(value = """
    SELECT u FROM User u
    WHERE u.id IN (
        SELECT ur.friendId FROM UserRelation ur
        WHERE ur.userId = :userId AND ur.isAccepted = false
    )""")
    List<User> findAllReceiversByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
    UserRelation findByUserIdAndFriendId(Long userId, Long friendId);
}
