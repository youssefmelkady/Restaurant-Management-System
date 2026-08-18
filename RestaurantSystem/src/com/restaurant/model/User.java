package com.restaurant.model;

import com.restaurant.enums.Role;
import java.time.LocalDate;

public abstract class User {
    protected String id;
    protected String username;
    protected String password;
    protected LocalDate dateOfBirth;
    protected Role role;

    public User(String id, String username, String password, LocalDate dateOfBirth, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Role getRole() { return role; }

    // Password validation: min 8 chars, one uppercase, one digit, one special char
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    // Phone validation: Egyptian format 01XXXXXXXXX (11 digits)
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("01[0125]\\d{8}");
    }
}
