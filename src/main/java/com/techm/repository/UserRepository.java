
package com.techm.repository;

import com.techm.entity.Users;
import com.techm.entity.Users.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
   
    Optional<Users> findByRole(Users.Role role); // Ensure this exists

	boolean existsByRole(Role admin);

	
}
