package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugStatus;

import java.time.LocalDateTime;

public class ResolvedState extends BugState {

    public ResolvedState(Bug bug) {
        super(bug);
    }

    @Override
    public void assign(User developer) {
        throw new IllegalStateException("Çözülmüş bug tekrar atanamaz!");
    }

    @Override
    public void resolve() {
        throw new IllegalStateException("Bug zaten çözülmüş!");
    }

    @Override
    public void close() {
        System.out.println("Bug kapatılıyor...");
        bug.setStatus(BugStatus.CLOSED);
        bug.setClosedAt(LocalDateTime.now());
        changeState(new ClosedState(bug));
    }

    @Override
    public void reopen() {
        System.out.println("Bug yeniden açılıyor...");
        bug.setStatus(BugStatus.REOPENED);
        bug.setResolvedAt(null);
        changeState(new ReopenedState(bug));
    }

    @Override
    public String getStateName() {
        return "RESOLVED";
    }
}