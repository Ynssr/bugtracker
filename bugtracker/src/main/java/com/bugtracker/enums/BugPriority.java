package com.bugtracker.enums;

public enum BugPriority {
    LOW("Düşük", 1),
    MEDIUM("Orta", 2),
    HIGH("Yüksek", 3),
    CRITICAL("Kritik", 4);

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
