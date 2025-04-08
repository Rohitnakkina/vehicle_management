
package com.techm.repository;

import com.techm.entity.ServiceAdvisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceAdvisorRepository extends JpaRepository<ServiceAdvisor, Long> {
    Optional<ServiceAdvisor> findByEmail(String email);
}

