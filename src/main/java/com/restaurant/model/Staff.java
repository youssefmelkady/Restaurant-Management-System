package com.restaurant.model;

import com.restaurant.enums.Role;
import java.time.LocalDate;

public abstract class Staff extends User {
    private int workingHours;

    public Staff(String id, String username, String password,
                 LocalDate dateOfBirth, Role role, int workingHours) {
        super(id, username, password, dateOfBirth, role);
        this.workingHours = workingHours;
    }

    public int getWorkingHours() { return workingHours; }
    public void setWorkingHours(int workingHours) { this.workingHours = workingHours; }
}