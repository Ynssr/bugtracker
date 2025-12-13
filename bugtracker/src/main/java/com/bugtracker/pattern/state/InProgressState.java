package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugStatus;

import java.time.LocalDateTime;

public class InProgressState extends BugState {

    public InProgressState(Bug bug) {
        super(bug);
    }

    @Override
    public void assign(User developer) {
        System.out.println("Bug farklı bir developer'a atanıyor: " + developer.getUsername());
        bug.setAssignee(developer);
    }

    @Override
    public void resolve() {
        System.out.println("Bug çözüldü olarak işaretleniyor...");
        bug.setStatus(BugStatus.Resolved);
        bug.setResolvedAt(LocalDateTime.now());
        changeState(new ResolvedState(bug));
    }

    @Override
    public void close() {
        throw new IllegalStateException("Devam eden bug çözülmeden kapatılamaz! Önce çözülmeli.");
    }

    @Override
    public void reopen() {
        throw new IllegalStateException("Bug zaten devam ediyor!");
    }

    @Override
    public String getStateName() {
        return "In_progress";
    }
}