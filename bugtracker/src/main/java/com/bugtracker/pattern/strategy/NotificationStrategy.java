package com.bugtracker.pattern.strategy;

import com.bugtracker.entity.User;

public interface NotificationStrategy {
    void sendNotification(User user, String message);
}