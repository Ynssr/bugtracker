package com.bugtracker.entity;

import com.bugtracker.enums.BugPriority;
import com.bugtracker.enums.BugSeverity;
import com.bugtracker.enums.BugStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bugs")
@Data
@NoArgsConstructor
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status = BugStatus.Open;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugSeverity severity;

    // İlişkiler - Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Bug'ı raporlayan kişi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee; // Bug'a atanan kişi (Developer)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // Hangi projeye ait

    // Tarihler
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Ek bilgiler
    @Column(name = "steps_to_reproduce", columnDefinition = "TEXT")
    private String stepsToReproduce;

    @Column(name = "expected_behavior", columnDefinition = "TEXT")
    private String expectedBehavior;

    @Column(name = "actual_behavior", columnDefinition = "TEXT")
    private String actualBehavior;

    @Column(name = "environment")
    private String environment; // Production, Staging, Development

    // Constructor
    public Bug(String title, String description, BugPriority priority,
               BugSeverity severity, User reporter) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.reporter = reporter;
        this.status = BugStatus.Open;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business Logic Methods
    public void assignTo(User developer) {
        if (developer.getRole().name().equals("DEVELOPER")) {
            this.assignee = developer;
            this.status = BugStatus.In_progress;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void resolve() {
        this.status = BugStatus.Resolved;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.status = BugStatus.Reopened;
        this.resolvedAt = null;
        this.closedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = BugStatus.Closed;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BugStatus.Open;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}