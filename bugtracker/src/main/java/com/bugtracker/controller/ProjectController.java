package com.bugtracker.controller;

import com.bugtracker.entity.Project;
import com.bugtracker.entity.User;
import com.bugtracker.service.ProjectService;
import com.bugtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/key/{key}")
    public ResponseEntity<Project> getProjectByKey(@PathVariable String key) {
        return projectService.getProjectByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        return ResponseEntity.ok(projectService.getActiveProjects());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam String keyword) {
        return ResponseEntity.ok(projectService.searchProjectsByName(keyword));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(id, project);
        return ResponseEntity.ok(updatedProject);
    }
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProject(@PathVariable Long id) {
        projectService.deactivateProject(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        projectService.deleteProject(id, user);
        return ResponseEntity.noContent().build();
    }
}