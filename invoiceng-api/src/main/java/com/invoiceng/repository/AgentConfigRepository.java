package com.invoiceng.repository;

import com.invoiceng.entity.AgentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentConfigRepository extends JpaRepository<AgentConfig, UUID> {

    Optional<AgentConfig> findByBusinessId(UUID businessId);

    boolean existsByBusinessId(UUID businessId);
}
