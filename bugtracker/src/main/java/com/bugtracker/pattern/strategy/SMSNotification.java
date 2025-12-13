package com.bugtracker.pattern.strategy;

import com.bugtracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SMSNotification implements NotificationStrategy {

    @Override
    public void sendNotification(User user, String message) {
        System.out.println("SMS gönderiliyor...");
        System.out.println("Alıcı: " + user.getUsername());
        System.out.println("Mesaj: " + message);
        System.out.println("SMS başarıyla gönderildi!\n");
    }
}