package com.restaurant.model;

import com.restaurant.enums.Role;
import java.time.LocalDate;

public class Waiter extends Staff {
    private String assignedSection;

    public Waiter(String id, String username, String password,
                  LocalDate dateOfBirth, int workingHours, String assignedSection) {
        super(id, username, password, dateOfBirth, Role.WAITER, workingHours);
        this.assignedSection = assignedSection;
    }

    public String getAssignedSection() { return assignedSection; }
    public void setAssignedSection(String s) { this.assignedSection = s; }
}