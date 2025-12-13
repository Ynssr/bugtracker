package com.bugtracker.entity;

import com.bugtracker.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Tester")
@Getter
@Setter
@NoArgsConstructor
public class Tester extends User {

    @Column(name = "testing_type")
    private String testingType; // Manual, Automation, Performance

    public Tester(String username, String email, String password) {
        super(username, email, password, UserRole.Tester);
    }

    @Override
    public boolean canDeleteBug() {
        return false; // Tester bug silemez
    }

    @Override
    public boolean canAssignBug() {
        return false; // Tester bug atayamaz
    }

    @Override
    public boolean canCloseBug() {
        return false; // Tester bug kapatamaz, sadece raporlar
    }
}