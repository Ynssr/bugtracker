package com.bugtracker.pattern.strategy;

import com.bugtracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InAppNotification implements NotificationStrategy {

    @Override
    public void sendNotification(User user, String message) {
        System.out.println("Uygulama içi bildirim gönderiliyor...");
        System.out.println("Kullanıcı: " + user.getUsername());
        System.out.println("Mesaj: " + message);
        System.out.println("Bildirim başarıyla gönderildi!\n");
    }
}