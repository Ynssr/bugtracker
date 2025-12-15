package com.bugtracker.repository;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugPriority;
import com.bugtracker.enums.BugStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    List<Bug> findByStatus(BugStatus status);

    List<Bug> findByPriority(BugPriority priority);

    List<Bug> findByReporter(User reporter);

    List<Bug> findByAssignee(User assignee);

    List<Bug> findByProjectId(Long projectId);

    List<Bug> findByTitleContainingIgnoreCase(String keyword);

    List<Bug> findByStatusAndPriority(BugStatus status, BugPriority priority);

    List<Bug> findByAssigneeIsNull();

    @Query("SELECT b FROM Bug b WHERE b.assignee = :user AND b.status IN ('Open', 'In_progress', 'Reopened')")
    List<Bug> findActiveBugsByAssignee(@Param("user") User user);

    @Query("SELECT b FROM Bug b WHERE b.status = 'Open' AND b.priority IN ('High', 'Critical')")
    List<Bug> findHighPriorityOpenBugs();

    long countByStatus(BugStatus status);

    long countByAssignee(User assignee);
}