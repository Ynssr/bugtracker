package com.bugtracker.entity;

import com.bugtracker.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("REPORTER")
@NoArgsConstructor
public class Reporter extends User {

    public Reporter(String username, String email, String password) {
        super(username, email, password, UserRole.Reporter);
    }

    @Override
    public boolean canDeleteBug() {
        return false; // Reporter sadece kendi oluşturduğu bug'ı silebilir (bu logic service'de)
    }

    @Override
    public boolean canAssignBug() {
        return false; // Reporter bug atayamaz
    }

    @Override
    public boolean canCloseBug() {
        return false; // Reporter bug kapatamaz
    }
}