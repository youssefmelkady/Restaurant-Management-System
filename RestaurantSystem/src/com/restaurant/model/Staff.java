package com.restaurant.model;

import com.restaurant.enums.Role;
import java.time.LocalDate;

public abstract class Staff extends User {
    protected int workingHours;

    public Staff(String id, String username, String password, LocalDate dateOfBirth, Role role, int workingHours) {
        super(id, username, password, dateOfBirth, role);
        this.workingHours = workingHours;
    }

    public int getWorkingHours()              { return workingHours; }
    public void setWorkingHours(int hours)    { this.workingHours = hours; }

    public static Staff login(String username, String password) {
        for (User u : com.restaurant.db.RestaurantDatabase.staffMembers) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                return (Staff) u;
            }
        }
        return null;
    }
}
