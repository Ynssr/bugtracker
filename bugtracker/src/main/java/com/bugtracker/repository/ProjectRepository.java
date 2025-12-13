package com.bugtracker.repository;

import com.bugtracker.entity.Project;
import com.bugtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);

    Optional<Project> findByProjectKey(String projectKey);

    List<Project> findByOwner(User owner);

    List<Project> findByIsActiveTrue();

    boolean existsByName(String name);

    boolean existsByProjectKey(String projectKey);

    List<Project> findByNameContainingIgnoreCase(String keyword);
}