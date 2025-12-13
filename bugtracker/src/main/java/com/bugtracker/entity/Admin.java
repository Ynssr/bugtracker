package com.bugtracker.entity;

import com.bugtracker.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Admin")
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username, String email, String password) {
        super(username, email, password, UserRole.Admin);
    }

    @Override
    public boolean canDeleteBug() {
        return true; // Admin her bug'ı silebilir
    }

    @Override
    public boolean canAssignBug() {
        return true; // Admin bug atayabilir
    }

    @Override
    public boolean canCloseBug() {
        return true; // Admin bug kapatabilir
    }
}