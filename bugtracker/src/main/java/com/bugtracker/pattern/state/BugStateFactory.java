package com.bugtracker.pattern.state;

import com.bugtracker.entity.Bug;
import com.bugtracker.enums.BugStatus;

public class BugStateFactory {

    public static BugState createState(Bug bug) {
        BugStatus status = bug.getStatus();

        return switch (status) {
            case Open -> new OpenState(bug);
            case In_progress -> new InProgressState(bug);
            case Resolved -> new ResolvedState(bug);
            case Reopened -> new ReopenedState(bug);
            case Closed -> new ClosedState(bug);
        };
    }
}