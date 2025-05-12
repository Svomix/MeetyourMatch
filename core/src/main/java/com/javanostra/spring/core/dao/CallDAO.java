package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallDAO extends JpaRepository<Call, Long> {
    Optional<Call> findByCallId(String callId);
    List<Call> findByCallerIdOrCalleeIdAndStatus(String callerId, String calleeId, Call.CallStatus status);
    @Query("SELECT c FROM Call c WHERE " +
           "(c.callerId = :userId OR c.calleeId = :userId) " +
           "AND c.status = 'ACTIVE'")
    List<Call> findActiveCallsByUser(@Param("userId") String userId);
}