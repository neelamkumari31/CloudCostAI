package com.finops.cloudcost;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WastedServerRepository extends JpaRepository<WastedServer, Long> {
    // Custom database query enabling Java to lookup server rows using the unique secure string token
    Optional<WastedServer> findByApprovalToken(String token);
}
