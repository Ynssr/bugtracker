package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;

public abstract class BugState {

    protected Bug bug;

    public BugState(Bug bug) {
        this.bug = bug;
    }
    public abstract void assign(User developer);

    public abstract void resolve();

    public abstract void close();

    public abstract void reopen();

    public abstract String getStateName();

    protected void changeState(BugState newState) {
        System.out.println("Durum değişiyor: " + getStateName() + " -> " + newState.getStateName());
    }
}