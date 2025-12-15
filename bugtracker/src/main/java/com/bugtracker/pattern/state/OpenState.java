package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.enums.BugStatus;

public class OpenState extends BugState {

    public OpenState(Bug bug) {
        super(bug);
    }

    @Override
    public void assign(User developer) {
        System.out.println("Bug " + developer.getUsername() + " kullanıcısına atanıyor...");
        bug.setAssignee(developer);
        bug.setStatus(BugStatus.IN_PROGRESS);
        changeState(new InProgressState(bug));
    }

    @Override
    public void resolve() {
        throw new IllegalStateException("Açık bug developera atanmadan çözülemez. Önce bir devolepera atayınız.");
    }

    @Override
    public void close() {
        throw new IllegalStateException("Açık bug doğrudan kapatılamaz!");
    }

    @Override
    public void reopen() {
        throw new IllegalStateException("Bug zaten açık!");
    }

    @Override
    public String getStateName() {
        return "OPEN";
    }
}