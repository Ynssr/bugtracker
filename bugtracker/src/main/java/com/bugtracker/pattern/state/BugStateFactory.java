package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.enums.BugStatus;

public class BugStateFactory {

    public static BugState createState(Bug bug) {
        BugStatus status = bug.getStatus();

        return switch (status) {
            case OPEN -> new OpenState(bug);
            case IN_PROGRESS -> new InProgressState(bug);
            case RESOLVED -> new ResolvedState(bug);
            case REOPENED -> new ReopenedState(bug);
            case CLOSED -> new ClosedState(bug);
        };
    }
}