package com.bugtracker.repository;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.BugActivity;
import com.bugtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugActivityRepository extends JpaRepository<BugActivity, Long> {

    List<BugActivity> findByBugOrderByCreatedAtDesc(Bug bug);

    List<BugActivity> findByUserOrderByCreatedAtDesc(User user);

    List<BugActivity> findByActivityType(String activityType);
}