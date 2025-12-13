package com.bugtracker.pattern.observer;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;

public interface BugObserver {
    void onBugCreated(Bug bug, User creator);
    void onBugUpdated(Bug bug, User updater, String changeDescription);
    void onBugAssigned(Bug bug, User assignee, User assigner);
    void onBugResolved(Bug bug, User resolver);
    void onBugClosed(Bug bug, User closer);
    void onBugReopened(Bug bug, User reopener);
}