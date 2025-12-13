package com.bugtracker.pattern.factory;

import com.bugtracker.entity.Bug;
import java.util.List;

public interface Report {
    String generate(List<Bug> bugs);
    String getFormat();
}