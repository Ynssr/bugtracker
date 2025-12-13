package com.bugtracker.service;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Project;
import com.bugtracker.entity.User;
import com.bugtracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    // Oluşturma
    public Project createProject(Project project) {
        // Doğrulama
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Proje adı boş olamaz!");
        }

        if (projectRepository.existsByName(project.getName())) {
            throw new IllegalArgumentException("Bu proje adı zaten kullanılıyor!");
        }

        if (project.getProjectKey() == null || project.getProjectKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Proje anahtarı boş olamaz!");
        }

        if (projectRepository.existsByProjectKey(project.getProjectKey())) {
            throw new IllegalArgumentException("Bu proje anahtarı zaten kullanılıyor!");
        }

        return projectRepository.save(project);
    }

    // Read
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByName(String name) {
        return projectRepository.findByName(name);
    }

    public Optional<Project> getProjectByKey(String projectKey) {
        return projectRepository.findByProjectKey(projectKey);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByIsActiveTrue();
    }

    public List<Project> getProjectsByOwner(User owner) {
        return projectRepository.findByOwner(owner);
    }

    // Güncelleme
    public Project updateProject(Long id, Project updatedProject) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proje bulunamadı!"));

        // İsim değişiyorsa kontrol et
        if (!existingProject.getName().equals(updatedProject.getName())) {
            if (projectRepository.existsByName(updatedProject.getName())) {
                throw new IllegalArgumentException("Bu proje adı zaten kullanılıyor!");
            }
            existingProject.setName(updatedProject.getName());
        }

        existingProject.setDescription(updatedProject.getDescription());

        return projectRepository.save(existingProject);
    }

    // Sil
    public void deleteProject(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proje bulunamadı!"));

        // Sadece proje sahibi veya admin silebilir
        if (!project.getOwner().equals(user) && !user.canDeleteBug()) {
            throw new IllegalArgumentException("Bu projeyi silme yetkiniz yok!");
        }

        projectRepository.deleteById(id);
    }

    // Deaktif et
    public void deactivateProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proje bulunamadı!"));

        project.setActive(false);
        projectRepository.save(project);
    }

    // Projeye bug ekle
    public Project addBugToProject(Long projectId, Bug bug) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proje bulunamadı!"));

        project.addBug(bug);
        return projectRepository.save(project);
    }

    // Arama
    public List<Project> searchProjectsByName(String keyword) {
        return projectRepository.findByNameContainingIgnoreCase(keyword);
    }
}