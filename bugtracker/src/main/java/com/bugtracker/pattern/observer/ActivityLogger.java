package com.bugtracker.pattern.observer;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.BugActivity;
import com.bugtracker.entity.User;
import com.bugtracker.repository.BugActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityLogger implements BugObserver {

    private final BugActivityRepository activityRepository;

    @Override
    public void onBugCreated(Bug bug, User creator) {
        BugActivity activity = new BugActivity(
                bug,
                creator,
                "Created",
                "Bug oluşturuldu: " + bug.getTitle()
        );
        activityRepository.save(activity);
        System.out.println("Bug oluşturuldu " + creator.getUsername() + "kullanıcısı tarafından");
    }

    @Override
    public void onBugUpdated(Bug bug, User updater, String changeDescription) {
        BugActivity activity = new BugActivity(
                bug,
                updater,
                "Updates",
                "Bug güncellendi: " + changeDescription
        );
        activityRepository.save(activity);
        System.out.println("Bug güncellendi " + updater.getUsername() + "kullanıcısı tarafından");
    }

    @Override
    public void onBugAssigned(Bug bug, User assignee, User assigner) {
        BugActivity activity = new BugActivity(
                bug,
                assigner,
                "Assigned",
                bug.getTitle() + " bug'ı " + assignee.getUsername() + " kullanıcısına atandı",
                null,
                assignee.getUsername()
        );
        activityRepository.save(activity);
        System.out.println("Bug atandı " + assignee.getUsername() + "kullanıcısına");
    }

    @Override
    public void onBugResolved(Bug bug, User resolver) {
        BugActivity activity = new BugActivity(
                bug,
                resolver,
                "Resolved",
                "Bug çözüldü"
        );
        activityRepository.save(activity);
        System.out.println("Bug çözüldü " + resolver.getUsername() + "kullanıcısı tarafından");
    }

    @Override
    public void onBugClosed(Bug bug, User closer) {
        BugActivity activity = new BugActivity(
                bug,
                closer,
                "Closed",
                "Bug kapatıldı"
        );
        activityRepository.save(activity);
        System.out.println("Bug kapatıldı " + closer.getUsername() + "kullanıcısı tarafından");
    }

    @Override
    public void onBugReopened(Bug bug, User reopener) {
        BugActivity activity = new BugActivity(
                bug,
                reopener,
                "Reopened",
                "Bug yeniden açıldı"
        );
        activityRepository.save(activity);
        System.out.println("Bug yeniden açıldı " + reopener.getUsername() + "kullanıcısı tarafından");
    }
}