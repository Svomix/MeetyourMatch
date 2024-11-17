package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityDAO extends JpaRepository<UserAuthority, Integer> {
    UserAuthority findByAuthority(String authority);
    boolean existsByAuthority(String authority);
    //Set<UserAuthority> findByAuthorityIn(List<String> authorities);
}
