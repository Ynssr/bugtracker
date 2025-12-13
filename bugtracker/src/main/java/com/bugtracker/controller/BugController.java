package com.bugtracker.controller;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugPriority;
import com.bugtracker.enums.BugStatus;
import com.bugtracker.service.BugService;
import com.bugtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BugController {

    private final BugService bugService;
    private final UserService userService;

    //Oluşturma

    @PostMapping
    public ResponseEntity<Bug> createBug(@RequestBody Bug bug, @RequestParam Long reporterId) {
        User reporter = userService.getUserById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        bug.setReporter(reporter);
        Bug createdBug = bugService.createBug(bug, reporter);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBug);
    }
    //Okma
    @GetMapping
    public ResponseEntity<List<Bug>> getAllBugs() {
        return ResponseEntity.ok(bugService.getAllBugs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable Long id) {
        return bugService.getBugById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bug>> getBugsByStatus(@PathVariable BugStatus status) {
        return ResponseEntity.ok(bugService.getBugsByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Bug>> getBugsByPriority(@PathVariable BugPriority priority) {
        return ResponseEntity.ok(bugService.getBugsByPriority(priority));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Bug>> getUnassignedBugs() {
        return ResponseEntity.ok(bugService.getUnassignedBugs());
    }

    @GetMapping("/high-priority")
    public ResponseEntity<List<Bug>> getHighPriorityBugs() {
        return ResponseEntity.ok(bugService.getHighPriorityOpenBugs());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Bug>> searchBugs(@RequestParam String keyword) {
        return ResponseEntity.ok(bugService.searchBugsByTitle(keyword));
    }

    //Güncelleme

    @PutMapping("/{id}")
    public ResponseEntity<Bug> updateBug(
            @PathVariable Long id,
            @RequestBody Bug bug,
            @RequestParam Long updaterId) {

        User updater = userService.getUserById(updaterId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        Bug updatedBug = bugService.updateBug(id, bug, updater);
        return ResponseEntity.ok(updatedBug);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Bug> assignBug(
            @PathVariable Long id,
            @RequestParam Long developerId,
            @RequestParam Long assignerId) {

        User developer = userService.getUserById(developerId)
                .orElseThrow(() -> new IllegalArgumentException("Developer bulunamadı!"));

        User assigner = userService.getUserById(assignerId)
                .orElseThrow(() -> new IllegalArgumentException("Atayan kullanıcı bulunamadı!"));

        Bug assignedBug = bugService.assignBug(id, developer, assigner);
        return ResponseEntity.ok(assignedBug);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Bug> resolveBug(@PathVariable Long id, @RequestParam Long resolverId) {
        User resolver = userService.getUserById(resolverId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        Bug resolvedBug = bugService.resolveBug(id, resolver);
        return ResponseEntity.ok(resolvedBug);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Bug> closeBug(@PathVariable Long id, @RequestParam Long closerId) {
        User closer = userService.getUserById(closerId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        Bug closedBug = bugService.closeBug(id, closer);
        return ResponseEntity.ok(closedBug);
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<Bug> reopenBug(@PathVariable Long id, @RequestParam Long reopenerId) {
        User reopener = userService.getUserById(reopenerId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        Bug reopenedBug = bugService.reopenBug(id, reopener);
        return ResponseEntity.ok(reopenedBug);
    }

    //Silme

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        bugService.deleteBug(id, user);
        return ResponseEntity.noContent().build();
    }

    //İstatilsk

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable BugStatus status) {
        return ResponseEntity.ok(bugService.countBugsByStatus(status));
    }
}