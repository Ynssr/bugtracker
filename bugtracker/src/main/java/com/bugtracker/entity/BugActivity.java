package com.bugtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bug_activities")
@Data
@NoArgsConstructor
public class BugActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id", nullable = false)
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Kim yaptı?

    @Column(nullable = false, length = 50)
    private String activityType; // CREATED, UPDATED, ASSIGNED, RESOLVED, etc.

    @Column(columnDefinition = "Text")
    private String description; // Ne oldu?

    @Column(name = "old_value")
    private String oldValue; // Eski değer

    @Column(name = "new_value")
    private String newValue; // Yeni değer

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BugActivity(Bug bug, User user, String activityType, String description) {
        this.bug = bug;
        this.user = user;
        this.activityType = activityType;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public BugActivity(Bug bug, User user, String activityType,
                       String description, String oldValue, String newValue) {
        this.bug = bug;
        this.user = user;
        this.activityType = activityType;
        this.description = description;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}