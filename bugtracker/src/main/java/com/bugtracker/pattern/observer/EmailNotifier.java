package com.bugtracker.pattern.observer;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements BugObserver {

    @Override
    public void onBugCreated(Bug bug, User creator) {
        System.out.println("Email: Yeni bug oluşturuldu - " + bug.getTitle());
    }

    @Override
    public void onBugUpdated(Bug bug, User updater, String changeDescription) {
        if (bug.getAssignee() != null) {
            System.out.println("Email gönderiliyor: " + bug.getAssignee().getEmail());
            System.out.println("Mesaj: Bug güncellendi - " + changeDescription);
        }
    }

    @Override
    public void onBugAssigned(Bug bug, User assignee, User assigner) {
        System.out.println("📧 Email gönderiliyor: " + assignee.getEmail());
        System.out.println("   Mesaj: Size yeni bir bug atandı - " + bug.getTitle());
    }

    @Override
    public void onBugResolved(Bug bug, User resolver) {
        if (bug.getReporter() != null) {
            System.out.println("Email gönderiliyor: " + bug.getReporter().getEmail());
            System.out.println("Mesaj: Raporladığınız bug çözüldü - " + bug.getTitle());
        }
    }

    @Override
    public void onBugClosed(Bug bug, User closer) {
        System.out.println("Email: Bug kapatıldı - " + bug.getTitle());
    }

    @Override
    public void onBugReopened(Bug bug, User reopener) {
        if (bug.getAssignee() != null) {
            System.out.println("Email gönderiliyor: " + bug.getAssignee().getEmail());
            System.out.println("Mesaj: Bug yeniden açıldı - " + bug.getTitle());
        }
    }
}