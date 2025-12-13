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

    // Status'e göre bug bulma
    List<Bug> findByStatus(BugStatus status);

    // Priority'ye göre bug bulma
    List<Bug> findByPriority(BugPriority priority);

    // Reporter'a göre bug bulma
    List<Bug> findByReporter(User reporter);

    // Assignee'ye göre bug bulma
    List<Bug> findByAssignee(User assignee);

    // Project ID'ye göre bug bulma
    List<Bug> findByProjectId(Long projectId);

    // Başlıkta arama
    List<Bug> findByTitleContainingIgnoreCase(String keyword);

    // Status ve Priority kombinasyonu
    List<Bug> findByStatusAndPriority(BugStatus status, BugPriority priority);

    // Atanmamış bug'lar
    List<Bug> findByAssigneeIsNull();

    // Custom JPQL query - Bir kullanıcıya atanan açık bug'lar
    @Query("SELECT b FROM Bug b WHERE b.assignee = :user AND b.status IN ('Open', 'In_progress', 'Reopened')")
    List<Bug> findActiveBugsByAssignee(@Param("user") User user);

    // Yüksek öncelikli açık bug'lar
    @Query("SELECT b FROM Bug b WHERE b.status = 'Open' AND b.priority IN ('High', 'Critical')")
    List<Bug> findHighPriorityOpenBugs();

    // Bug sayısı istatistiği
    long countByStatus(BugStatus status);

    long countByAssignee(User assignee);
}