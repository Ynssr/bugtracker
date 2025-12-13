package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugStatus;

public class ClosedState extends BugState {

    public ClosedState(Bug bug) {
        super(bug);
    }

    @Override
    public void assign(User developer) {
        throw new IllegalStateException("Kapalı bug atanamaz!");
    }

    @Override
    public void resolve() {
        throw new IllegalStateException("Bug zaten kapalı!");
    }

    @Override
    public void close() {
        throw new IllegalStateException("Bug zaten kapalı!");
    }

    @Override
    public void reopen() {
        System.out.println("Kapalı bug yeniden açılıyor...");
        bug.setStatus(BugStatus.Reopened);
        bug.setClosedAt(null);
        changeState(new ReopenedState(bug));
    }

    @Override
    public String getStateName() {
        return "Closed";
    }
}