package com.bugtracker.entity;

import com.bugtracker.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Developer")
@Getter
@Setter
@NoArgsConstructor
public class Developer extends User {

    @Column(name = "specialization")
    private String specialization; // Backend, Frontend, Fullstack

    @Column(name = "experience_years")
    private Integer experienceYears;

    public Developer(String username, String email, String password) {
        super(username, email, password, UserRole.DEVELOPER);
    }

    @Override
    public boolean canDeleteBug() {
        return false;
    }

    @Override
    public boolean canAssignBug() {
        return false;
    }

    @Override
    public boolean canCloseBug() {
        return true;
    }
}