package com.examregistration.backend.repository;

import com.examregistration.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Not strictly necessary for JpaRepository, but good practice
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Custom query method
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}