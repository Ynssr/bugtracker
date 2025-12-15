package com.bugtracker.entity;

import com.bugtracker.enums.BugPriority;
import com.bugtracker.enums.BugSeverity;
import com.bugtracker.enums.BugStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private BugStatus status = BugStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugSeverity severity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    @JsonIgnoreProperties({"reportedBugs", "assignedBugs", "password", "hibernateLazyInitializer"})
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignee_id")
    @JsonIgnoreProperties({"reportedBugs", "assignedBugs", "password", "hibernateLazyInitializer"})
    private User assignee; //Developer

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tester_id")
    @JsonIgnoreProperties({"reportedBugs", "assignedBugs", "password", "hibernateLazyInitializer"})
    private User tester; //TEster

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"bugs", "owner", "hibernateLazyInitializer"})
    private Project project;

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
    private String environment;

    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("bug")
    private List<BugActivity> activities = new ArrayList<>();

    public Bug(String title, String description, BugPriority priority,
               BugSeverity severity, User reporter) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.reporter = reporter;
        this.status = BugStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void assignTo(User developer) {
        if (developer.getRole().name().equals("DEVELOPER")) {
            this.assignee = developer;
            this.status = BugStatus.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void assignTester(User tester) {
        if (tester.getRole().name().equals("TESTER") || tester.getRole().name().equals("REPORTER")) {
            this.tester = tester;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void resolve() {
        this.status = BugStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.status = BugStatus.REOPENED;
        this.resolvedAt = null;
        this.closedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = BugStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BugStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
