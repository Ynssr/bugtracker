package com.bugtracker.enums;

public enum BugPriority {
    Low("Düşük", 1),
    Medium("Orta", 2),
    High("Yüksek", 3),
    Critical("Kritik", 4);

    private final String displayName;
    private final int level;

    BugPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}