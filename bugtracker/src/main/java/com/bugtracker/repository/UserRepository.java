package com.bugtracker.repository;

import com.bugtracker.entity.User;
import com.bugtracker.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query methods - Spring otomatik implement eder

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByIsActiveTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByFullNameContainingIgnoreCase(String name);
}