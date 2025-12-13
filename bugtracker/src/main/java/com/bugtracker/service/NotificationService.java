package com.bugtracker.service;

import com.bugtracker.entity.User;
import com.bugtracker.pattern.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private NotificationStrategy strategy;

    // Strategy'yi runtime'da değiştirme
    public void setStrategy(NotificationStrategy strategy) {
        this.strategy = strategy;
    }

    public void notifyUser(User user, String message) {
        if (strategy == null) {
            throw new IllegalStateException("Bildirim stratejisi ayarlanmamış!");
        }
        strategy.sendNotification(user, message);
    }

    // Toplu bildirim
    public void notifyMultipleUsers(java.util.List<User> users, String message) {
        if (strategy == null) {
            throw new IllegalStateException("Bildirim stratejisi ayarlanmamış!");
        }

        for (User user : users) {
            strategy.sendNotification(user, message);
        }
    }
}