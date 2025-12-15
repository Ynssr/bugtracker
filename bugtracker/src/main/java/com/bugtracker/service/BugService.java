package com.bugtracker.service;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugPriority;
import com.bugtracker.enums.BugStatus;
import com.bugtracker.repository.BugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BugService {

    private final BugRepository bugRepository;

    public Bug createBug(Bug bug, User creator) {
        if (bug.getTitle() == null || bug.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Bug başlığı boş olamaz!");
        }

        if (bug.getPriority() == null) {
            throw new IllegalArgumentException("Bug önceliği belirtilmelidir!");
        }

        if (bug.getSeverity() == null) {
            throw new IllegalArgumentException("Bug önem derecesi belirtilmelidir!");
        }

        Bug savedBug = bugRepository.save(bug);
        return savedBug;
    }

    public Optional<Bug> getBugById(Long id) {
        return bugRepository.findById(id);
    }

    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public List<Bug> getBugsByStatus(BugStatus status) {
        return bugRepository.findByStatus(status);
    }

    public List<Bug> getBugsByPriority(BugPriority priority) {
        return bugRepository.findByPriority(priority);
    }

    public List<Bug> getBugsByReporter(User reporter) {
        return bugRepository.findByReporter(reporter);
    }

    public List<Bug> getBugsByAssignee(User assignee) {
        return bugRepository.findByAssignee(assignee);
    }

    public List<Bug> getUnassignedBugs() {
        return bugRepository.findByAssigneeIsNull();
    }

    public List<Bug> getActiveBugsByAssignee(User assignee) {
        return bugRepository.findActiveBugsByAssignee(assignee);
    }

    public List<Bug> getHighPriorityOpenBugs() {
        return bugRepository.findHighPriorityOpenBugs();
    }

    public Bug updateBug(Long id, Bug updatedBug, User updater) {
        Bug existingBug = bugRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        StringBuilder changes = new StringBuilder();

        if (!existingBug.getTitle().equals(updatedBug.getTitle())) {
            changes.append("Başlık değiştirildi. ");
            existingBug.setTitle(updatedBug.getTitle());
        }

        if (!existingBug.getDescription().equals(updatedBug.getDescription())) {
            changes.append("Açıklama güncellendi. ");
            existingBug.setDescription(updatedBug.getDescription());
        }

        if (existingBug.getPriority() != updatedBug.getPriority()) {
            changes.append("Öncelik değiştirildi: ")
                    .append(existingBug.getPriority())
                    .append(" -> ")
                    .append(updatedBug.getPriority())
                    .append(". ");
            existingBug.setPriority(updatedBug.getPriority());
        }

        if (existingBug.getSeverity() != updatedBug.getSeverity()) {
            changes.append("Önem derecesi değiştirildi. ");
            existingBug.setSeverity(updatedBug.getSeverity());
        }

        existingBug.setStepsToReproduce(updatedBug.getStepsToReproduce());
        existingBug.setExpectedBehavior(updatedBug.getExpectedBehavior());
        existingBug.setActualBehavior(updatedBug.getActualBehavior());
        existingBug.setEnvironment(updatedBug.getEnvironment());

        Bug savedBug = bugRepository.save(existingBug);

        if (changes.length() > 0) {
            String changeDescription = changes.toString();
        }

        return savedBug;
    }
    
    public Bug assignBug(Long bugId, User developer, User assigner) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        if (!developer.getRole().name().equals("DEVELOPER")) {
            throw new IllegalArgumentException("Sadece developer'lara bug atanabilir!");
        }

        bug.assignTo(developer);
        Bug savedBug = bugRepository.save(bug);
        return savedBug;
    }

    public Bug resolveBug(Long bugId, User resolver) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        if (bug.getAssignee() == null) {
            throw new IllegalArgumentException("Bu bug henüz atanmamış!");
        }
        
        if (!bug.getAssignee().getId().equals(resolver.getId())) {
            throw new IllegalArgumentException("Bu bug'ı sadece atanan developer çözebilir!");
        }

        bug.resolve();
        Bug savedBug = bugRepository.save(bug);
        return savedBug;
    }

    public Bug reopenBug(Long bugId, User reopener) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        boolean isAdmin = reopener.getRole().name().equals("ADMIN");
        boolean isTester = reopener.getRole().name().equals("TESTER");
        
        if (!isAdmin && !isTester) {
            throw new IllegalArgumentException("Bu bug'ı yeniden açma yetkiniz yok! Sadece Tester veya Admin açabilir.");
        }

        bug.reopen();
        Bug savedBug = bugRepository.save(bug);
        return savedBug;
    }

    public Bug closeBug(Long bugId, User closer) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        boolean isAdmin = closer.getRole().name().equals("ADMIN");
        boolean isTester = closer.getRole().name().equals("TESTER");
        
        if (!isAdmin && !isTester) {
            throw new IllegalArgumentException("Bu bug'ı kapatma yetkiniz yok! Sadece Tester veya Admin kapatabilir.");
        }

        bug.close();
        Bug savedBug = bugRepository.save(bug);

        return savedBug;
    }

    public void deleteBug(Long bugId, User user) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug bulunamadı!"));

        if (!user.canDeleteBug()) {
            throw new IllegalArgumentException("Bu bug'ı silme yetkiniz yok!");
        }

        bugRepository.deleteById(bugId);
    }

    public List<Bug> searchBugsByTitle(String keyword) {
        return bugRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public long countBugsByStatus(BugStatus status) {
        return bugRepository.countByStatus(status);
    }

    public long countBugsByAssignee(User assignee) {
        return bugRepository.countByAssignee(assignee);
    }

}
