package com.bugtracker.pattern.strategy;

import com.bugtracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmailNotification implements NotificationStrategy {

    @Override
    public void sendNotification(User user, String message) {
        // Gerçek uygulamada email gönderme kodu olurdu
        System.out.println("EMAIL gönderiliyor...");
        System.out.println("Alıcı: " + user.getEmail());
        System.out.println("Mesaj: " + message);
        System.out.println("Email başarıyla gönderildi!\n");
    }
}