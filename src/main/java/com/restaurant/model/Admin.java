package com.restaurant.model;

import com.restaurant.enums.Role;
import java.time.LocalDate;

public class Admin extends Staff {

    public Admin(String id, String username, String password,
                 LocalDate dateOfBirth, int workingHours) {
        super(id, username, password, dateOfBirth, Role.ADMIN, workingHours);
    }

    public boolean canManageMenu() { return true; }
}