package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugStatus;

import java.time.LocalDateTime;

public class ReopenedState extends BugState {

    public ReopenedState(Bug bug) {
        super(bug);
    }

    @Override
    public void assign(User developer) {
        System.out.println("Yeniden açılan bug " + developer.getUsername() + " kullanıcısına atanıyor...");
        bug.setAssignee(developer);
        bug.setStatus(BugStatus.IN_PROGRESS);
        changeState(new InProgressState(bug));
    }

    @Override
    public void resolve() {
        System.out.println("Bug tekrar çözüldü olarak işaretleniyor...");
        bug.setStatus(BugStatus.RESOLVED);
        bug.setResolvedAt(LocalDateTime.now());
        changeState(new ResolvedState(bug));
    }

    @Override
    public void close() {
        throw new IllegalStateException("Yeniden açılan bug doğrudan kapatılamaz! Önce çözülmeli.");
    }

    @Override
    public void reopen() {
        throw new IllegalStateException("Bug zaten yeniden açılmış!");
    }

    @Override
    public String getStateName() {
        return "Reopened";
    }
}